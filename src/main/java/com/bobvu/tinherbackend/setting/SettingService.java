package com.bobvu.tinherbackend.setting;

import com.bobvu.tinherbackend.cassandra.model.User;

public interface SettingService {
    void saveSetting(User user, UserSettingDTO req);

}
