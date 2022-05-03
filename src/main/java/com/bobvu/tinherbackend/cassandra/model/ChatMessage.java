package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;


@Table
@Builder
@Data

public class ChatMessage {
    @PrimaryKey
    private CMKey key;
    private String authorId;
    private String text;
    private Set<Seener> seeners = new HashSet<>();

    public long getLastMessageTime(){
        return this.key.getLastMessageTime();
    }

    public String getConversationId(){
        return this.key.getConversationId();
    }

    public void setLastMessageTime(long lmt){
        this.key.setLastMessageTime(lmt);
    }
    public void setConversationId(String convId){
        this.key.setConversationId(convId);
    }

}
