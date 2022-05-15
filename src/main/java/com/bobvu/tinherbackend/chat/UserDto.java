package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.User;

public class UserDto {
    private String username;
    private String fullName;

    private String avatar;



    public UserDto(User user) {
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.avatar = user.getAvatar();
    }
}
