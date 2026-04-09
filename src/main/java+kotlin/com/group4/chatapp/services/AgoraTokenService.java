package com.group4.chatapp.services;

import io.agora.media.RtcTokenBuilder2;
import io.agora.media.RtcTokenBuilder2.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgoraTokenService {

    @Value("${agora.app-id}")
    private String appId;

    @Value("${agora.app-certificate}")
    private String appCertificate;

    // Token valid for 2 hours
    private static final int TOKEN_EXPIRATION_IN_SECONDS = 7200;
    private static final int PRIVILEGE_EXPIRATION_IN_SECONDS = 7200;

    /**
     * Generates an Agora RTC token for a specific user to join a channel.
     * 
     * @param channelName The name of the channel.
     * @param uid The user ID (must be a 32-bit unsigned integer).
     * @return The generated token.
     */
    public String generateToken(String channelName, int uid) {
        RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();
        
        return tokenBuilder.buildTokenWithUid(
                appId, 
                appCertificate, 
                channelName, 
                uid, 
                Role.ROLE_PUBLISHER, 
                TOKEN_EXPIRATION_IN_SECONDS, 
                PRIVILEGE_EXPIRATION_IN_SECONDS
        );
    }
}
