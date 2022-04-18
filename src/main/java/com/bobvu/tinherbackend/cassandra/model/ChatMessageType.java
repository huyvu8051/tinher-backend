package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
@Data
@Builder
public class ChatMessageType {
    private long sentAt;
    private String conversationId;
    private String author;
    private String authorId;
    private String text;

    public ChatMessageType() {
    }

    public ChatMessageType(ChatMessage cm){
        this.sentAt = cm.getSentAt();
        this.conversationId = cm.getConversationId();
        this.author = cm.getAuthor();
        this.authorId = cm.getAuthorId();
        this.text = cm.getText();
    }

    public ChatMessageType(long now, String converId, String bà_mối, String system, String s) {
    }
}
