package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Builder
@UserDefinedType
public class UserPreferences {
    private  String displayName;
    private Photo avatarImage;
}
