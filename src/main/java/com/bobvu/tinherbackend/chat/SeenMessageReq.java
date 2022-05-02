package com.bobvu.tinherbackend.chat;

import lombok.Data;

@Data
public class SeenMessageReq {
    private long chatMessageId;
    private String converId;
}
