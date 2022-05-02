package com.bobvu.tinherbackend.chat;

import lombok.Data;

@Data
public class SentMessageReq {
    private String conversationId;
    private String chatMessage;

}
