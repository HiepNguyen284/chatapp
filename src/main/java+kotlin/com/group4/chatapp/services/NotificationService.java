package com.group4.chatapp.services;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.group4.chatapp.models.User;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(NotificationService.class);

  private final PresenceService presenceService;
  private final FcmTokenService fcmTokenService;
  private final UserService userService;

  public void sendPushIfOffline(String username, String title, String body,
                                Map<String, String> data) {
    if (presenceService.isAppActive(username)) {
      LOGGER.debug("User {} app is active, skipping FCM push", username);
      return;
    }

    LOGGER.info("Sending FCM push to user {}: {}", username, title);

    User user = userService.getUserByUsername(username).orElse(null);
    if (user == null) {
      LOGGER.warn("User not found for push notification: {}", username);
      return;
    }

    Long userId = user.getId();
    List<String> tokens = fcmTokenService.getTokensForUser(userId);
    if (tokens.isEmpty()) {
      LOGGER.warn("No FCM tokens for user {} (id={})", username, userId);
      return;
    }

    LOGGER.info("Sending FCM push to user {} ({} tokens)", username, tokens.size());

    Notification notification =
        Notification.builder().setTitle(title).setBody(body).build();

    AndroidConfig androidConfig =
        AndroidConfig.builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder()
                                 .setChannelId("chat_messages")
                                 .build())
            .build();

    for (String token : tokens) {
      Message message = Message.builder()
                            .setToken(token)
                            .setNotification(notification)
                            .setAndroidConfig(androidConfig)
                            .putAllData(data)
                            .build();

      try {
        FirebaseMessaging.getInstance().send(message);
      } catch (FirebaseMessagingException e) {
        LOGGER.warn("FCM push failed for user {}: {} - {}", username,
                    e.getErrorCode(), e.getMessage());
        if ("UNREGISTERED".equals(e.getErrorCode().name()) ||
            "INVALID_ARGUMENT".equals(e.getErrorCode().name())) {
          fcmTokenService.deleteInvalidToken(userId, token);
        }
      }
    }
  }

  public void pushNewMessage(String username, String senderName,
                             String messagePreview, long roomId) {
    sendPushIfOffline(username, senderName, messagePreview,
                      Map.of("type", "message", "roomId",
                             String.valueOf(roomId), "senderUsername",
                             senderName));
  }

  public void pushInvitation(String username, String senderName,
                             long invitationId, boolean isGroup) {
    String type = isGroup ? "group_invitation" : "invitation";
    String title = isGroup ? "Group Invitation" : "Friend Request";
    sendPushIfOffline(username, title, senderName + " sent you a " + type,
                      Map.of("type", type, "invitationId",
                             String.valueOf(invitationId), "senderUsername",
                             senderName));
  }

  public void pushInvitationReply(String username, String receiverName,
                                  boolean accepted) {
    String title =
        accepted ? "Friend Request Accepted" : "Friend Request Rejected";
    String body = accepted ? receiverName + " accepted your friend request"
                           : receiverName + " rejected your friend request";
    sendPushIfOffline(username, title, body,
                      Map.of("type", "invitation_reply", "senderUsername",
                             receiverName, "accepted",
                             String.valueOf(accepted)));
  }

  public void pushGroupEvent(String username, String groupName,
                             String actorName, long roomId, String eventType) {
    sendPushIfOffline(username, groupName, actorName + " " + eventType,
                      Map.of("type", eventType, "roomId",
                             String.valueOf(roomId), "senderUsername",
                             actorName));
  }
}
