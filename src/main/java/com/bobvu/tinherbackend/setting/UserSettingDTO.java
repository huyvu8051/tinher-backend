package com.bobvu.tinherbackend.setting;

import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserSettingDTO {

    private String about;
    private List<String> images;
    private List<Passion> passions;
    private Gender gender;
    private List<Gender> lookingFor;
    private double lat;
    private double lon;
    private int distancePreference;
    private List<Integer> agePreference;
    private int yearOfBirth;
}
