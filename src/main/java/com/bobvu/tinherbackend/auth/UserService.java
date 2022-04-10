package com.bobvu.tinherbackend.auth;

import com.bobvu.tinherbackend.cassandra.model.User;

public interface UserService {
    User createNewUser(String email, String fullname, String avatarUrl);

    void updateUserLocation(double lat, double lon, String username);
}
