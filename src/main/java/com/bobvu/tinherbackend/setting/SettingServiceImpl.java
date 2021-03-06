package com.bobvu.tinherbackend.setting;

import com.bobvu.tinherbackend.cassandra.mapper.UserMapper;
import com.bobvu.tinherbackend.cassandra.model.Image;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.bobvu.tinherbackend.elasticsearch.UserESRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserESRepository userEsRepo;

    @Autowired
    private UserMapper usrMap;

    @Override
    public void saveSetting(User user, UserSettingDTO req) {
        List<Image> images = req.getImages();

        if(images.size() > 0){
            user.setAvatar(images.get(0).getUrl());
        }

        usrMap.setting(req, user);
        userRepo.save(user);

        com.bobvu.tinherbackend.elasticsearch.User userEs = userEsRepo.findById(user.getUsername()).orElseThrow(() -> new NullPointerException("Username not found"));
        usrMap.setting(req, userEs);




        userEsRepo.save(userEs);


    }
}
