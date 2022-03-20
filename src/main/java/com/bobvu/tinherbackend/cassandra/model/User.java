package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table
@Builder
@Data
public class User {

    @Id
    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;
    private String partition;
    private String username;

    private UserPreferences userPreferences;

    private Long lastSeenAt;

    private List<Conversation> conversations;

    private String presence;
}
