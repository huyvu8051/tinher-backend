package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import com.bobvu.tinherbackend.cassandra.repository.ChatMessageRepository;
import com.bobvu.tinherbackend.cassandra.repository.ConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private SocketIOServer server;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserConversationRepository userConversationRepository;

    Faker faker = new Faker(new Locale("vi-VN"));

    @Override
    public String createNewConversation(User creator, String conversationName) {

        log.info("==============createNewConversation==============");
        long thisTime = System.currentTimeMillis();

        String conversationId = generateConversationId();




        ChatMessageType lm = new ChatMessageType();

        lm.setSentAt(thisTime);
        lm.setAuthor(creator.getFullName());
        lm.setText("You create a new conversation");

        Member mem = Member.builder()
                .memberShipStatus("creator")
                .username(creator.getUsername())
                .fullName(creator.getFullName())
                .build();


        Conversation con = Conversation.builder()
                .lastMessageTime(thisTime)
                .conversationId(conversationId)
                .userId(creator.getUsername())

                .build();

        conversationRepository.save(con);

        UserConversation userCon = UserConversation.builder()
                .userId(creator.getUsername())
                .conversationId(conversationId)
                .lastMessageTime(thisTime)
                .conversationName(conversationName)
                .lastMessage(lm)
                .members(Arrays.asList(mem))
                .memberIds(Arrays.asList(creator.getUsername()))
                .build();
        userConversationRepository.save(userCon);


        this.inviteUserToConversation(creator, creator, conversationId);

        return conversationId;

    }

    public UserConversation findConversationById(String userId, String conversationId) {

        UserConversation uc = userConversationRepository.findOneByUserIdAndConversationId(userId, conversationId);


        return uc;

    }


    @Override
    public void inviteUserToConversation(User inviter, User invitee, String conversationId) {
        log.info("==============inviteUserToConversation==============");
        UserConversation uCon = userConversationRepository.findOneByUserIdAndConversationId(inviter.getUsername(), conversationId);



        List<Member> members = new ArrayList<>(uCon.getMembers());

        Member newMem = Member.builder()
                .username(invitee.getUsername())
                .fullName(invitee.getFullName())
                .memberShipStatus("Member")
                .build();

        members.add(newMem);
        uCon.getMemberIds().add(invitee.getUsername());
        userConversationRepository.save(uCon);

        // change user id to save another conversation of invitee
        uCon.setUserId(invitee.getUsername());
        userConversationRepository.save(uCon);

        Conversation con = Conversation.builder()
                .userId(uCon.getUserId())
                .conversationId(uCon.getConversationId())
                .lastMessageTime(uCon.getLastMessageTime())
                .build();

        conversationRepository.save(con);


        this.sendMessage(inviter, conversationId, inviter.getFullName() + " invited " + invitee.getFullName(), System.currentTimeMillis());

    }


    private String generateConversationId() {
        return UUID.randomUUID().toString();
    }


    public void sendMessage(User sender, String conversationId, String text, long thisTime) {
        log.info("==============sendMessage==============");

        // save new chat message
        ChatMessage cm = ChatMessage.builder()
                .sentAt(thisTime)
                .conversationId(conversationId)
                .author(sender.getFullName())
                .authorId(sender.getUsername())
                .text(text).build();


        chatMessageRepository.save(cm);

        sendConversationMessage(sender, conversationId, cm);
    }

    private void sendConversationMessage(User sender, String conversationId, ChatMessage cm) {
        log.info("==============sendConversationMessage==============");
        UserConversation ucon = userConversationRepository.findOneByUserIdAndConversationId(sender.getUsername(), conversationId);

        List<String> userIds = new ArrayList<>(ucon.getMemberIds());

        long thisTime = System.currentTimeMillis();

        ChatMessageType cmt = new ChatMessageType(cm);

        userConversationRepository.updateLastMessageTime(userIds, conversationId, thisTime, cmt);

        List<Conversation> cons = new ArrayList<>();
        for(String id : userIds){
            cons.add(Conversation.builder()
                            .conversationId(conversationId)
                            .userId(id)
                            .lastMessageTime(thisTime)
                    .build());
        }

        conversationRepository.saveAll(cons);
        conversationRepository.deleteAllByIdsAndLastMessageTime(userIds, ucon.getLastMessageTime());



       List<User> users = userRepo.findAllById(userIds);

       for(User u : users){
           if(u.getSocketId() != null){

               SocketIOClient client = server.getClient(UUID.fromString(u.getSocketId()));
               if(client != null){
                   client.sendEvent("receiveMessage",conversationId, cm);
               }
           }
       }
    }


    @Override
    public List<UserConversation> getAllConversation(String userId, Pageable pageable) {


        Slice<Conversation> userCons = conversationRepository.findAllByUserId(userId, pageable);

        List<String> cons = userCons.stream().map(e -> e.getConversationId()).collect(Collectors.toList());

        List<UserConversation> result = userConversationRepository.findAllByUserIdAndConversationIds(userId, cons);

        return result;

    }

    @Override
    public List<ChatMessage> getAllChatMessageInConversation(String conversationId, int page, int size) {

        Slice<ChatMessage> slide = chatMessageRepository.findAllByConversationId(conversationId, Pageable.ofSize(page * size));

        List<ChatMessage> result = slide.stream().skip((page - 1) * size).collect(Collectors.toList());

        return result;
    }


    @Override
    public void seenAMessage(User seenBy, ChatMessage chatMessage) {

    }

    @Override
    public List<UserAvatarUrl> getUserAvatarUrls(List<String> userIds) {
        List<User> users = userRepo.findAllById(userIds);
        return users.stream().map(e->new UserAvatarUrl(e.getUsername(), e.getAvatar())).collect(Collectors.toList());
    }

    @Override
    public String createNewConversation(User creator, User invitee) {
        String converId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        ChatMessageType cmt = new  ChatMessageType(now, converId, "Bà mối", "System", "Bắt đầu nhắn tin nhia :3");

        UserConversation uc0 = new UserConversation(creator.getUsername(),converId,now,invitee.getFullName(),cmt)

// create conversation

        return null;
    }


}
