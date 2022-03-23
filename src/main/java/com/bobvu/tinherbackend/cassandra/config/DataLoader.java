package com.bobvu.tinherbackend.cassandra.config;

import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.chat.ChatService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    private ChatService chatService;

    private  Faker faker;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    faker = new Faker(new Locale("vi-VN"));

        User user0 = User.builder()
                .id(UUID.randomUUID().toString())
                .username(faker.name().username())
                .lastSeenAt(System.nanoTime())
                .build();


        User user1 = User.builder()
                .id(UUID.randomUUID().toString())
                .username(faker.name().username())
                .lastSeenAt(System.nanoTime()).
                build();


    }


}
