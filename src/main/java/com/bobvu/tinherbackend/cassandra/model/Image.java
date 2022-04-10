package com.bobvu.tinherbackend.cassandra.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@UserDefinedType
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private String url;
    private String alt;

    public Image(String url){
        this.url = url;
    }

}
