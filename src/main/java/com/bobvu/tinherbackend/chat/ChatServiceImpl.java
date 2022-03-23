package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import com.bobvu.tinherbackend.cassandra.repository.ChatMessageRepository;
import com.bobvu.tinherbackend.cassandra.repository.ConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService{


    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserConversationRepository userConversationRepository;
    @Override
    public Conversation createNewConversation(User creator, String conversationName) {
        Member mem = Member.builder()
                .userId(creator.getId())
                .username(creator.getUsername())
                .memberShipStatus("Creator")
                .build();

        Conversation con = Conversation.builder()
                .id("CONVER-" + UUID.randomUUID())
                .conversationName(conversationName)
                .members(Arrays.asList(mem ))
                .build();


        createUserConversations(Arrays.asList(mem ), con, creator.getUsername() + " created a new conversation name " + conversationName, "System");


        return conversationRepository.save(con);
    }

    private void createUserConversations(List<Member> members, Conversation con, String lastMessage, String lastMessageSender){

        List<UserConversation> userCons = new ArrayList<>();

        long thisTime = System.currentTimeMillis();

        for(Member mem : members){
            UserConversation uc = UserConversation.builder()
                    .clusterKey(con.getId() + "-" + System.currentTimeMillis())
                    .userId(mem.getUserId())
                    .conversationId(con.getId())
                    .createTime(System.currentTimeMillis())
                    .lastMessageText(lastMessage)
                    .lastMessageSender(lastMessageSender)
                    .conversationName(con.getConversationName())
                    .build();

            userCons.add(uc);
            userConversationRepository.saveAll(userCons);
            deleteAllOldUserCon(members, con.getId(),thisTime );

        }


    }


    private void deleteAllOldUserCon(List<Member> members, String conversationId, Long timeStamp){

        List<String> collect = members.stream().map(e -> e.getUserId()).collect(Collectors.toList());

        String minConId = conversationId + "-0000000000000";

        String maxConId = conversationId + "-" + timeStamp;

        userConversationRepository.deleteAllByIds(collect, minConId, maxConId);
    }

    @Override
    public Conversation inviteUserToConversation(User inviter, User invitee, Conversation conversation) {
        List<Member> members = new ArrayList<>(conversation.getMembers());

        Member newMem = Member.builder().userId(invitee.getId())
                .username(invitee.getUsername())
                .memberShipStatus("Member").build();
        members.add(newMem);

        return conversationRepository.save(conversation);
    }

    @Override
    public List<UserConversation> getAllConversation(String userId, Pageable pageable) {

        List<UserConversation> userCons = userConversationRepository.findAllByUserId(userId);

        return userCons;
    }

    @Override
    public List<ChatMessage> getAllChatMessageInConversation(String conversationId, Pageable pageable) {

        List<ChatMessage> allByConversationId = chatMessageRepository.findAllByConversationId(conversationId, pageable);


        return allByConversationId;
    }

    @Override
    public void sendAMessageToAConversation(User sender, String text, Conversation conversation) {
        ChatMessage cm = ChatMessage.builder()
                .sentAt(System.currentTimeMillis())
                .conversationId(conversation.getId())
                .author(sender.getUsername())
                .text(text)
                .build();


        chatMessageRepository.save(cm);
        createUserConversations(conversation.getMembers(), conversation, text, sender.getUsername());

    }

    @Override
    public void seenAMessage(User seenBy, ChatMessage chatMessage) {

    }
}
