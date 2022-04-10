package com.bobvu.tinherbackend.cassandra.mapper.impl;

import com.bobvu.tinherbackend.cassandra.mapper.UserMapper;
import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Image;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.match.ProfileResponse;
import com.bobvu.tinherbackend.setting.UserSettingDTO;
import org.elasticsearch.common.geo.GeoPoint;
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
    public ProfileResponse toProfileResponse(com.bobvu.tinherbackend.elasticsearch.User entity) {
        if ( entity == null ) {
            return null;
        }

        ProfileResponse profileResponse = new ProfileResponse();

        profileResponse.setUsername( entity.getUsername() );
        profileResponse.setFullName( entity.getFullName() );
        profileResponse.setAvatar( entity.getAvatar() );
        profileResponse.setLastSeenAt( entity.getLastSeenAt() );
        profileResponse.setAbout( entity.getAbout() );
        List<Image> list = entity.getImages();
        if ( list != null ) {
            profileResponse.setImages( new ArrayList<Image>( list ) );
        }
        List<Passion> list1 = entity.getPassions();
        if ( list1 != null ) {
            profileResponse.setPassions( new ArrayList<Passion>( list1 ) );
        }
        profileResponse.setGender( entity.getGender() );
        profileResponse.setLocation( entity.getLocation() );
        profileResponse.setYearOfBirth( entity.getYearOfBirth() );

        return profileResponse;
    }

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
            List<Image> list = request.getImages();
            if (list != null) {
                user.getImages().clear();
                user.getImages().addAll(list);
            } else {
                user.setImages(null);
            }
        } else {
            List<Image> list = request.getImages();
            if (list != null) {
                user.setImages(new ArrayList<Image>(list));
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


    @Override
    public void setting(UserSettingDTO request, com.bobvu.tinherbackend.elasticsearch.User user) {
        if ( request == null ) {
            return;
        }

        GeoPoint gp = new GeoPoint(request.getLat(), request.getLon());
        user.setLocation(gp);

        if ( request.getAbout() != null ) {
            user.setAbout( request.getAbout() );
        }
        if ( user.getImages() != null ) {
            List<Image> list = request.getImages();
            if ( list != null ) {
                user.getImages().clear();
                user.getImages().addAll( list );
            }
        }
        else {
            List<Image> list = request.getImages();
            if ( list != null ) {
                user.setImages( new ArrayList<Image>( list ) );
            }
        }
        if ( user.getPassions() != null ) {
            List<Passion> list1 = request.getPassions();
            if ( list1 != null ) {
                user.getPassions().clear();
                user.getPassions().addAll( list1 );
            }
        }
        else {
            List<Passion> list1 = request.getPassions();
            if ( list1 != null ) {
                user.setPassions( new ArrayList<Passion>( list1 ) );
            }
        }
        if ( request.getGender() != null ) {
            user.setGender( request.getGender() );
        }
        user.setYearOfBirth( request.getYearOfBirth() );
    }


    @Override
    public com.bobvu.tinherbackend.elasticsearch.User toEsEntity(com.bobvu.tinherbackend.cassandra.model.User user) {
        if ( user == null ) {
            return null;
        }

        com.bobvu.tinherbackend.elasticsearch.User.UserBuilder user1 = com.bobvu.tinherbackend.elasticsearch.User.builder();

        double lon = user.getLon();
        double lat = user.getLat();

        GeoPoint geoPoint = new GeoPoint(lat, lon);

        user1.location(geoPoint);

        user1.username( user.getUsername() );
        user1.fullName( user.getFullName() );
        user1.avatar( user.getAvatar() );
        user1.lastSeenAt( user.getLastSeenAt() );
        user1.about( user.getAbout() );
        List<Image> list = user.getImages();
        if ( list != null ) {
            user1.images( new ArrayList<Image>( list ) );
        }
        List<Passion> list1 = user.getPassions();
        if ( list1 != null ) {
            user1.passions( new ArrayList<Passion>( list1 ) );
        }
        user1.gender( user.getGender() );
        user1.yearOfBirth( user.getYearOfBirth() );

        return user1.build();
    }
}
