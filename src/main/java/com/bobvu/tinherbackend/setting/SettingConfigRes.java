package com.bobvu.tinherbackend.setting;

import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SettingConfigRes {
    private List<Passion> sysPassions;
    private List<Gender> sysGenders;

    // private UserSetting

    private UserSettingDTO userSetting;


}
