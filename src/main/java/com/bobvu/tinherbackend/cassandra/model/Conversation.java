package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table
@Data
@Builder
public class Conversation {

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private String conversationId;

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private String userId;


    private String conversationName;//


    private Long lastMessageAt;//

    private String lastMessageText;//
    private String lastMessageSender;//


    private List<Member> members;//




}
