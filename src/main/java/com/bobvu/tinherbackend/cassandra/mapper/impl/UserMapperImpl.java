package com.bobvu.tinherbackend.cassandra.mapper.impl;

import com.bobvu.tinherbackend.cassandra.mapper.UserMapper;
import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.setting.UserSettingDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2022-03-31T10:33:30+0700", comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)")
@Primary
@Component("myUserMapper")
public class UserMapperImpl implements UserMapper {

    @Override
    public void setting(UserSettingDTO request, User user) {
        if (request == null) {
            return;
        }
// user create
        if (request.getAgePreference() != null && request.getAgePreference().size() == 2) {

            user.setMinAge(request.getAgePreference().get(0));
            user.setMaxAge(request.getAgePreference().get(1));
        }

        user.setAbout(request.getAbout());
        if (user.getImages() != null) {
            List<String> list = request.getImages();
            if (list != null) {
                user.getImages().clear();
                user.getImages().addAll(list);
            } else {
                user.setImages(null);
            }
        } else {
            List<String> list = request.getImages();
            if (list != null) {
                user.setImages(new ArrayList<String>(list));
            }
        }
        if (user.getPassions() != null) {
            List<Passion> list1 = request.getPassions();
            if (list1 != null) {
                user.getPassions().clear();
                user.getPassions().addAll(list1);
            } else {
                user.setPassions(null);
            }
        } else {
            List<Passion> list1 = request.getPassions();
            if (list1 != null) {
                user.setPassions(new ArrayList<Passion>(list1));
            }
        }
        user.setGender(request.getGender());
        if (user.getLookingFor() != null) {
            List<Gender> list2 = request.getLookingFor();
            if (list2 != null) {
                user.getLookingFor().clear();
                user.getLookingFor().addAll(list2);
            } else {
                user.setLookingFor(null);
            }
        } else {
            List<Gender> list2 = request.getLookingFor();
            if (list2 != null) {
                user.setLookingFor(new ArrayList<Gender>(list2));
            }
        }
        user.setLat(request.getLat());
        user.setLon(request.getLon());
        user.setDistancePreference(request.getDistancePreference());
        user.setYearOfBirth(request.getYearOfBirth());
    }
}
