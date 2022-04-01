package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import com.bobvu.tinherbackend.cassandra.repository.ChatMessageRepository;
import com.bobvu.tinherbackend.cassandra.repository.ConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserConversationRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {


    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserConversationRepository userConversationRepository;

    Faker faker = new Faker(new Locale("vi-VN"));

    @Override
    public String createNewConversation(User creator, String conversationName) {


        long thisTime = System.currentTimeMillis();

        String conversationId = generateConversationId();


        UserConversation userCon = UserConversation.builder()
                .userId(creator.getId())
                .conversationId(conversationId)
                .lastMessageTime(thisTime)
                .build();

        ChatMessageType lm = new ChatMessageType();

        lm.setSentAt(thisTime);
        lm.setAuthor(creator.getFullName());
        lm.setText("You create a new conversation");

        Member mem = Member.builder()
                .memberShipStatus("creator")
                .userId(creator.getId())
                .fullName(creator.getFullName())
                .build();


        Conversation con = Conversation.builder()
                .lastMessageTime(thisTime)
                .conversationId(conversationId)
                .userId(creator.getId())
                .conversationName(conversationName)
                .lastMessage(lm)
                .members(Arrays.asList(mem))
                .memberIds(Arrays.asList(creator.getId()))
                .build();

        conversationRepository.save(con);
        userConversationRepository.save(userCon);


        this.inviteUserToConversation(creator, creator, conversationId);

        return conversationId;

    }

    public Conversation findConversationById(String userId, String conversationId) {

        UserConversation uc = userConversationRepository.findOneByUserIdAndConversationId(userId, conversationId);

        Conversation con = conversationRepository.findOneByUserIdAndLastMessageTime(userId, uc.getLastMessageTime());
        return con;

    }


    @Override
    public void inviteUserToConversation(User inviter, User invitee, String conversationId) {

        Conversation con = this.findConversationById(inviter.getId(), conversationId);


        List<Member> members = new ArrayList<>(con.getMembers());

        Member newMem = Member.builder()
                .userId(invitee.getId())
                .fullName(invitee.getFullName())
                .memberShipStatus("Member")
                .build();

        members.add(newMem);
        con.getMemberIds().add(invitee.getId());
        conversationRepository.save(con);

        // change user id to save another conversation of invitee
        con.setUserId(invitee.getId());
        conversationRepository.save(con);

        UserConversation uCon = UserConversation.builder()
                .userId(con.getUserId())
                .conversationId(con.getConversationId())
                .lastMessageTime(con.getLastMessageTime())
                .build();

        userConversationRepository.save(uCon);

        this.sendMessage(inviter, conversationId, inviter.getFullName() + " invited " + invitee.getFullName());

    }


    private String generateConversationId() {
        return UUID.randomUUID().toString();
    }


    public void sendMessage(User sender, String conversationId, String text) {
        long thisTime = System.currentTimeMillis();


        // save new chat message
        ChatMessage cm = ChatMessage.builder().sentAt(thisTime).conversationId(conversationId).author(sender.getFullName()).text(text).build();


        chatMessageRepository.save(cm);

        sendConversationMessage(sender, conversationId, cm);
    }

    private void sendConversationMessage(User sender, String conversationId, ChatMessage cm) {
        Conversation con = this.findConversationById(sender.getId(), conversationId);

        if(con == null){
            con.getConversationId();
        }

        List<String> userIds = new ArrayList<>(con.getMemberIds());


        long thisTime = System.currentTimeMillis();


        List<Conversation> cons = conversationRepository.findAllByUserIdsAndLastMessageTime(userIds, con.getLastMessageTime());

        ChatMessageType cmt = new ChatMessageType(cm);


        List<Conversation> collect = cons.stream().map(e -> {
            e.setLastMessageTime(thisTime);
            e.setLastMessage(cmt);
            return e;
        }).collect(Collectors.toList());

        conversationRepository.saveAll(collect);

        userConversationRepository.updateLastMessageTime(userIds, conversationId, thisTime);

        conversationRepository.deleteAllByIdsAndLastMessageTime(userIds, con.getLastMessageTime());
    }


    @Override
    public List<Conversation> getAllConversation(String userId, Pageable pageable) {


        List<Conversation> userCons = conversationRepository.findAllByUserId(userId);

        return userCons;
    }

    @Override
    public List<ChatMessage> getAllChatMessageInConversation(String conversationId, Pageable pageable) {

        List<ChatMessage> allByConversationId = chatMessageRepository.findAllByConversationId(conversationId, pageable);

        return allByConversationId;
    }


    @Override
    public void seenAMessage(User seenBy, ChatMessage chatMessage) {

    }


}
