package com.bobvu.tinherbackend.cassandra.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table
@Builder
@Data
public class User implements UserDetails {

    @Id
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private String username;
    private String fullName;
    private String password;

    private String avatar;
    private long lastSeenAt;

    // description
    private String about;

    private List<Image> images;
    private List<Passion> passions;
    private Gender gender;

    private List<Gender> lookingFor;

    private double lat;
    private double lon;
    private int distancePreference;

    private int yearOfBirth;
    private int minAge;
    private int maxAge;


    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private List<String> roles;
    private String socketId;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
