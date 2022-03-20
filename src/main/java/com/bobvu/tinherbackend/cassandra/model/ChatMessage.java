package com.bobvu.tinherbackend.cassandra.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table
public class ChatMessage {

    @Id
    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;
    private  String partition;
    private String author;
    private String text;

    private Photo image;

    private List<Double> location;
    private Long timestamp;
}
