package com.bobvu.tinherbackend.cassandra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@NoArgsConstructor
@AllArgsConstructor

@UserDefinedType
public class Member {
    private String userId;
    private String userDisplayedId;
}
