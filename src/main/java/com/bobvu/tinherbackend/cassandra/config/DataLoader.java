package com.bobvu.tinherbackend.cassandra.config;

import com.bobvu.tinherbackend.cassandra.model.Conversation;
import com.bobvu.tinherbackend.cassandra.model.Member;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserPreferences;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        Faker faker = new Faker();

        User user0 = User.builder()
                .id(UUID.randomUUID().toString())
                .partition(faker.color().name())
                .username(faker.name().username())
                .lastSeenAt(System.nanoTime())
                .presence("Con chim")
                .userPreferences(UserPreferences.builder()
                        .displayName(faker.name().fullName())
                        .build())

                .build();


        User user1 = User.builder()
                .id(UUID.randomUUID().toString())
                .username(faker.name().username())
                .lastSeenAt(System.nanoTime()).
                build();

        List<Member> members = Arrays.asList(Member.builder()
                .username(user0.getUsername())
                .memberShipStatus("Bullshit")
                .build(), Member.builder()
                .username(user1.getUsername())
                .memberShipStatus("Simp")
                .build());

        Conversation conver0 = Conversation.builder()
                .id(UUID.randomUUID().toString())
                .displayName(user0.getUsername())
                .unreadCount(0d)
                .members(members)
                .build();

        Conversation conver1 = Conversation.builder()
                .id(UUID.randomUUID().toString())
                .displayName(user1.getUsername())
                .unreadCount(0d)
                .members(members)
                .build();


    }
}
