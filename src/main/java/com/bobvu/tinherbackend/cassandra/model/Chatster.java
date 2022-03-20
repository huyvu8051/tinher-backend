package com.bobvu.tinherbackend.cassandra.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Chatster {

    @Id
    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;
    private String partition;
    private String userName;
    private String displayName;

    private Photo avatarImage;
    private Long lastSeenAt;
    private String presence;
}
