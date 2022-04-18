package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
@Builder
@Data
public class Liked {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private String username;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private String likedTargetId;
}
