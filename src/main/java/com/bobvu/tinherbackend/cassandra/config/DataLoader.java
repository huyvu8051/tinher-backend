package com.bobvu.tinherbackend.cassandra.config;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.Conversation;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import com.bobvu.tinherbackend.chat.ChatService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
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

        Conversation conver1 = chatService.createNewConversation(user0, "conver1");
        chatService.inviteUserToConversation(user0,user1,conver1);

        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user0, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user0, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver1);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver1);

        List<ChatMessage> allChatMessageInConversation = chatService.getAllChatMessageInConversation(conver1.getId(), Pageable.unpaged());
        allChatMessageInConversation.forEach(e->{
            System.out.println(new Date(e.getSentAt()) + " by: " + e.getAuthor() + " text: " + e.getText());
        });

        System.out.println("=====================");

        List<UserConversation> userConversations = chatService.getAllConversation(user0.getId(), Pageable.unpaged());

        userConversations.forEach(e->{
            System.out.println(new Date(e.getCreateTime()) + " con: " + e.getConversationName() + " lastmess: " + e.getLastMessageText() + " by: " + e.getLastMessageSender()) ;
        });


        Conversation conver2 = chatService.createNewConversation(user0, "conver2");
        chatService.inviteUserToConversation(user1,user0,conver2);

        chatService.sendAMessageToAConversation(user0, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user0, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user0, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user0, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver2);
        chatService.sendAMessageToAConversation(user1, faker.lorem().sentence(), conver2);

        List<UserConversation> userConversations1 = chatService.getAllConversation(user0.getId(), Pageable.unpaged());
        System.out.println("=====================");
        userConversations1.forEach(e->{
            System.out.println(new Date(e.getCreateTime()) + " con: " + e.getConversationName() + " lastmess: " + e.getLastMessageText() + " by: " + e.getLastMessageSender()) ;
        });


    }


}
