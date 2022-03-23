package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@UserDefinedType
@Builder
@Data
public class Member {

    private String userId;
    private String username;
    private String memberShipStatus;

}
