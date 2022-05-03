package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;

@Table
@Data
@Builder
public class Conversation {
    @Id
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private String conversationId;
    private Set<Member> members;
    private long lastMessageTime;

}
