package com.bobvu.tinherbackend.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetAvatarUrlReq {
    private List<String> userIds;
}
