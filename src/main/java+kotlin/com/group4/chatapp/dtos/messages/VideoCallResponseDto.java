package com.group4.chatapp.dtos.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCallResponseDto {
    private String channelName;
    private String token;
    private long roomId;
    private int uid;
}
