package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table
@Data
@Builder
public class UserConversation {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private String userId;
    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private String conversationId;

    private long lastMessageTime;


    private String conversationName;//


    private ChatMessageType lastMessage;


    private List<Member> members;//

    private List<String> memberIds;
}
