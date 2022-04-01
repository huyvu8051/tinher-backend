package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
@Data
public class ChatMessageType {
    private long sentAt;
    private String conversationId;
    private String author;
    private String text;

    public ChatMessageType() {
    }

    public ChatMessageType(ChatMessage cm){
        this.sentAt = cm.getSentAt();
        this.conversationId = cm.getConversationId();
        this.author = cm.getAuthor();
        this.text = cm.getText();
    }
}
