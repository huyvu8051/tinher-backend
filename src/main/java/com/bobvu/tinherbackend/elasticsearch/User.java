package com.bobvu.tinherbackend.elasticsearch;

import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Image;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import java.util.List;

@Data
@Document(indexName = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String username;

    private List<Passion> passions;
    private Gender gender;

    private long boostTime;

    @GeoPointField
    private GeoPoint location;
    private int yearOfBirth;

}
