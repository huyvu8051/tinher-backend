package com.bobvu.tinherbackend.setting;

import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("setting")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @GetMapping
    public SettingConfigRes getAllSettingConfig() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Passion[] passions = Passion.class.getEnumConstants();
        Gender[] genders = Gender.class.getEnumConstants();

        UserSettingDTO ust = UserSettingDTO.builder().about(user.getAbout())
                .passions(user.getPassions())
                .distancePreference(user.getDistancePreference())
                .gender(user.getGender())
                .images(user.getImages())
                .lookingFor(user.getLookingFor())
                .agePreference(Arrays.asList(user.getMinAge(), user.getMaxAge()))
                .dateOfBirth(user.getDateOfBirth())
                .build();


        return SettingConfigRes.builder()
                .sysGenders(Arrays.asList(genders))
                .sysPassions(Arrays.asList(passions))
                .userSetting(ust)


                .build();

    }


    @PostMapping
    public void saveSetting(@RequestBody UserSettingDTO req){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        settingService.saveSetting(user, req);
    }

}
