package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserProfileUpdateDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.AttachmentRepository;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.MailException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final AttachmentRepository attachmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final FileTypeService fileTypeService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserCacheService userCacheService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    public void createUser(UserDto dto) {

        if (repository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists. Please try a different one."
            );
        }

        var hashedPassword = passwordEncoder.encode(dto.password());

        var user = User.builder()
            .username(dto.username())
            .password(hashedPassword)
            .displayName(dto.username())
            .build();

        repository.save(user);
    }

    public Optional<User> getUserByAuthentication(@Nullable Authentication authentication) {

        if (authentication == null) {
            return Optional.empty();
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String username = jwt.getSubject();
            return userCacheService.getCachedUser(username);
        } else if (principal instanceof User user) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByAuthentication(authentication);
    }

    public User getUserOrThrows() {
        return getUserByContext()
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.UNAUTHORIZED));
    }

    public Optional<User> getUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Transactional
    public void requestPasswordReset(String username) {
        var normalizedUsername = username.trim();
        if (normalizedUsername.isEmpty()) {
            return;
        }

        repository.findByUsername(normalizedUsername).ifPresent(user -> {
            var token = passwordResetTokenService.generateToken(user.getUsername());
            try {
                emailService.sendPasswordResetEmail(user.getUsername(), user.getUsername(), token);
            } catch (MailException ex) {
                passwordResetTokenService.revokeToken(token);
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Failed to send password reset email");
            }
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        validateNewPassword(newPassword);

        var username = passwordResetTokenService.validateToken(token);
        var user = repository.findByUsername(username)
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired password reset token"));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
        userCacheService.invalidateUserCache(user.getUsername());
        passwordResetTokenService.revokeByUsername(user.getUsername());
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        validateNewPassword(newPassword);

        var user = getUserOrThrows();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
        userCacheService.invalidateUserCache(user.getUsername());
        passwordResetTokenService.revokeByUsername(user.getUsername());
    }

    public Optional<User> getUserById(long userId) {
        return repository.findById(userId);
    }

    @Transactional(readOnly = true)
    public UserWithAvatarDto getCurrentProfile() {
        return new UserWithAvatarDto(getUserOrThrows());
    }

    @Transactional
    public UserWithAvatarDto updateCurrentProfile(UserProfileUpdateDto dto) {

        var user = getUserOrThrows();
        var changed = false;

        var nextDisplayName = normalizeDisplayName(dto.displayName());
        if (nextDisplayName != null && !nextDisplayName.equals(user.getDisplayName())) {
            user.setDisplayName(nextDisplayName);
            changed = true;
        }

        var nextAvatar = saveAvatar(dto.avatar());
        if (nextAvatar != null) {
            user.setAvatar(nextAvatar);
            changed = true;
        }

        if (changed) {
            user = repository.save(user);
            userCacheService.invalidateUserCache(user.getUsername());
            publishProfileUpdate(user);
        }

        return new UserWithAvatarDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserWithAvatarDto> searchUser(String keyword, int limit) {

        var MAX_LIMIT = 20;

        if (limit <= 0 || limit > MAX_LIMIT) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                String.format("Limit must be between 1 and %d", MAX_LIMIT)
            );
        }

        var normalizedKeyword = keyword.trim();
        if (normalizedKeyword.isEmpty()) {
            return List.of();
        }

        var currentUser = getUserOrThrows();
        var pageable = PageRequest.ofSize(limit);

        return repository.searchByKeyword(normalizedKeyword, currentUser.getId(), pageable)
            .stream()
            .map(UserWithAvatarDto::new)
            .toList();
    }

    private void publishProfileUpdate(User user) {
        messagingTemplate.convertAndSend(
            "/queue/users/profile/",
            new UserWithAvatarDto(user)
        );
    }

    @Nullable
    private String normalizeDisplayName(@Nullable String displayName) {
        if (displayName == null) {
            return null;
        }

        var trimmed = displayName.trim();

        if (trimmed.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Display name must not be blank");
        }

        if (trimmed.length() > 60) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Display name must be at most 60 characters");
        }

        return trimmed;
    }

    @Nullable
    private Attachment saveAvatar(@Nullable MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            return null;
        }

        var resourceType = fileTypeService.getMimeType(avatarFile.getContentType());
        var format = fileTypeService.getFileExtension(avatarFile.getOriginalFilename());
        var fileType = fileTypeService.checkTypeInFileType(resourceType, format);

        if (fileType != Attachment.FileType.IMAGE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Avatar must be an image");
        }

        var uploadedUrl = s3Service.uploadAvatar(avatarFile);
        var attachment = Attachment.of(uploadedUrl, Attachment.FileType.IMAGE);

        return attachmentRepository.save(attachment);
    }

    private void validateNewPassword(String password) {
        var normalized = password == null ? "" : password.trim();
        if (normalized.length() < 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
        }
    }
}
