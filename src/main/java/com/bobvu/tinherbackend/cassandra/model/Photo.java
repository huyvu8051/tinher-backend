package com.bobvu.tinherbackend.cassandra.model;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;


@UserDefinedType
public class Photo {

    private String id;
    private String thumbNail;
    private String picture;
    private Long date;
}
