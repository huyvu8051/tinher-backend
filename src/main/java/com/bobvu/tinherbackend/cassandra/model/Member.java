package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
@Builder
public class Member {

    private String username;
    private String memberShipStatus;
}
