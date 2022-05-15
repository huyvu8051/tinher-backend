package com.bobvu.tinherbackend.setting;

import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Image;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserSettingDTO {

    private String about;
    private List<Image> images;
    private List<Passion> passions;
    private Gender gender;
    private List<Gender> lookingFor;
    private double lat;
    private double lon;
    private int distancePreference;
    private List<Integer> agePreference;
    private LocalDate dateOfBirth;
}
