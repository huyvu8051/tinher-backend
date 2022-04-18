package com.bobvu.tinherbackend.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAvatarUrl {
    private String userid;
    private String avatarUrl;
}
