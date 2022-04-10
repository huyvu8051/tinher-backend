package com.bobvu.tinherbackend.match;

import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Image;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import lombok.Data;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import java.util.List;

@Data
public class ProfileResponse {

    @Id
    private String username;
    private String fullName;
    private String avatar;
    private long lastSeenAt;
    private String about;

    private List<Image> images;
    private List<Passion> passions;
    private Gender gender;

    @GeoPointField
    private GeoPoint location;
    private int yearOfBirth;


}