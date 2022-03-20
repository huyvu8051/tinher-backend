package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.util.List;

@Builder
@UserDefinedType
public class Conversation {

    private String id;
    private String displayName;
    private Double unreadCount;
    private List<Member> members;

}
