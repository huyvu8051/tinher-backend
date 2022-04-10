package com.bobvu.tinherbackend.cassandra.config;

import com.bobvu.tinherbackend.auth.UserService;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOServer;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private SocketIOServer server;

    @Autowired
    private UserRepository userRepo;


    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try {
            server.start();
            log.info("Socket launch successful!");
        } catch (Exception e) {
            log.error("Socket launch failure!");
            log.error(e.getMessage());
        }

        Faker faker= new Faker(new Locale("vi-VN"));

        List<User> all = userRepo.findAll();

        if( all.size() < 20) {
            for(int i = 0; i < 20; i++){
                userService.createNewUser(faker.name().username(), faker.name().fullName(), "https://i.pinimg.com/564x/61/e8/e7/61e8e7e634c7cf80dc255c93578ea56c.jpg");
            }
       }

    }




}
