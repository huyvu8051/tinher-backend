package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import com.bobvu.tinherbackend.cassandra.repository.*;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import sun.plugin2.message.Conversation;

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
    private OrderedConversationRepository orderedConverRepo;
    @Autowired
    private UserConversationRepository usrConRepo;

    Faker faker = new Faker(new Locale("vi-VN"));


    public UserConversation findConversationById(String userId, String conversationId) {

        UserConversation uc = usrConRepo.findOneByUserIdAndConversationId(userId, conversationId).orElseThrow(() -> new NullPointerException("Conversation not found"));


        return uc;

    }


    public void sendMessage(User sender, String conversationId, String text, long thisTime) {
        ChatMessage cm = saveNewChatMessage(sender, conversationId, text, thisTime);

        UserConversation uc = updateOrderedConversations(sender.getUsername(), conversationId, thisTime);

        User partner = userRepo.findById(uc.getPartnerId()).orElseThrow(() -> new NullPointerException("Partner not found"));


        noticeMessageViaSocket(conversationId, sender.getSocketId(), partner.getSocketId(), cm);
    }

    private ChatMessage saveNewChatMessage(User sender, String conversationId, String text, long thisTime) {
        log.info("==============sendMessage==============");

        Seener seener = Seener.builder()
                .username(sender.getUsername())
                .seenAt(thisTime)
                .build();

        Set<Seener> seeners = new HashSet<>();

        seeners.add(seener);

        // save new chat message

        CMKey key = new CMKey(thisTime, conversationId);

        ChatMessage cm = ChatMessage.builder()
                .key(key)
                .author(sender.getFullName())
                .authorId(sender.getUsername())
                .seeners(seeners)
                .text(text)
                .build();


        return chatMessageRepository.save(cm);
    }

    private UserConversation updateOrderedConversations(String senderId, String conversationId, long thisTime) {
        log.info("==============updateOrderedConversations==============");
        UserConversation userCon = usrConRepo.findOneByUserIdAndConversationId(senderId, conversationId).orElseThrow(() -> new NullPointerException("Conversation not found"));

        OrderedConversation o1 = OrderedConversation.builder()
                .conversationId(conversationId)
                .lastMessageTime(thisTime)
                .userId(senderId)

                .build();

        OrderedConversation o2 = OrderedConversation.builder()
                .conversationId(conversationId)
                .lastMessageTime(thisTime)
                .userId(userCon.getPartnerId())
                .build();

        orderedConverRepo.saveAll(Arrays.asList(o1, o2));
        orderedConverRepo.deleteAllByIdsAndLastMessageTime(Arrays.asList(senderId, userCon.getPartnerId()), userCon.getLastMessageTime());

        usrConRepo.updateLastMessageTime(Arrays.asList(senderId, userCon.getPartnerId()), conversationId, thisTime);


        return userCon;

    }

    private void noticeMessageViaSocket(String converId, String senderSocketId, String partnerSocketId, ChatMessage cm) {


        SocketIOClient client1 = server.getClient(UUID.fromString(senderSocketId));
        client1.sendEvent("receiveMessage", converId, cm);

        SocketIOClient client2 = server.getClient(UUID.fromString(partnerSocketId));
        client2.sendEvent("receiveMessage", converId, cm);


    }


    @Override
    public List<UserConversation> getAllConversation(String userId, Pageable pageable) {


        Slice<OrderedConversation> userCons = orderedConverRepo.findAllByUserId(userId, pageable);

        List<String> cons = userCons.stream().map(e -> e.getConversationId()).collect(Collectors.toList());

        List<UserConversation> result = usrConRepo.findAllByUserIdAndConversationIds(userId, cons);

        return result;

    }

    @Override
    public List<ChatMessage> getAllChatMessageInConversation(String conversationId, int page, int size) {

        Slice<ChatMessage> slide = chatMessageRepository.findAllByConversationId(conversationId, Pageable.ofSize(page * size));

        List<ChatMessage> result = slide.stream().skip((page - 1) * size).collect(Collectors.toList());

        return result;
    }


    @Override
    public void seenAMessage(User seenBy, String convId, long sentAt) {

        ChatMessage cm = chatMessageRepository.findOneById(convId, sentAt).orElseThrow(() -> new NullPointerException("ChatMessage not found"));

        Seener seener = Seener.builder()
                .username(seenBy.getUsername())
                .seenAt(System.currentTimeMillis())
                .build();

        cm.getSeeners().add(seener);
        chatMessageRepository.save(cm);

    }


    @Override
    public List<ChatMessage> findAllLastMessage(List<String> converIds) {

        List<UserConversation> convers = usrConRepo.findAllByUserId(converIds);

        List<String> ids = convers.stream().map(e -> e.getConversationId()).collect(Collectors.toList());

        List<Long> lastMessTimes = convers.stream().map(e -> e.getLastMessageTime()).collect(Collectors.toList());


        return chatMessageRepository.findAllLastMessage(ids, lastMessTimes);

    }

    @Override
    public List<User> getAllUserByConversationIds( List<String> userIds) {


        List<User> users = userRepo.findAllById(userIds);

        return users;
    }

    @Override
    public String createNewConversation(User creator, User invitee) {
        String converId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        UserConversation uc0 = UserConversation.builder()
                .userId(creator.getUsername())
                .conversationId(converId)
                .partnerId(invitee.getUsername())
                .lastMessageTime(now)
                .build();

        UserConversation uc1 = UserConversation.builder()
                .userId(invitee.getUsername())
                .conversationId(converId)
                .partnerId(creator.getUsername())
                .lastMessageTime(now)
                .build();


        usrConRepo.saveAll(Arrays.asList(uc1, uc0));

        OrderedConversation con0 = OrderedConversation.builder()
                .lastMessageTime(now)
                .userId(creator.getUsername())
                .conversationId(converId)
                .build();

        OrderedConversation con1 = OrderedConversation.builder()
                .lastMessageTime(now)
                .userId(invitee.getUsername())
                .conversationId(converId)
                .build();


        orderedConverRepo.saveAll(Arrays.asList(con0, con1));

        this.sendMessage(creator, converId, creator.getFullName() + " create a conversation with " + invitee.getFullName(), now + 1);

        return converId;
    }


}
