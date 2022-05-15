package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import com.bobvu.tinherbackend.cassandra.repository.ChatMessageRepository;
import com.bobvu.tinherbackend.cassandra.repository.ConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.OrderedConversationRepository;
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
    private OrderedConversationRepository orderedConverRepo;
    @Autowired
    private ConversationRepository conRepo;

    Faker faker = new Faker(new Locale("vi-VN"));


    public Conversation findConversationById(String userId, String conversationId) {

        Conversation uc = conRepo.findById(conversationId).orElseThrow(() -> new NullPointerException("Conversation not found"));


        return uc;

    }


    public void sendMessage(User sender, String conversationId, String text, long thisTime) {
        ChatMessage cm = saveNewChatMessage(sender, conversationId, text, thisTime);

        Conversation uc = updateOrderedConversations(conversationId, thisTime);

        List<String> memIds = uc.getMembers().stream().map(e -> e.getUserId()).collect(Collectors.toList());

        noticeMessageViaSocket( memIds, cm);
    }

    private ChatMessage saveNewChatMessage(User sender, String conversationId, String text, long thisTime) {
        log.info("==============sendMessage==============");

        Seener seener = Seener.builder().username(sender.getUsername()).seenAt(thisTime).build();

        Set<Seener> seeners = new HashSet<>();

        seeners.add(seener);

        // save new chat message

        CMKey key = new CMKey(thisTime, conversationId);

        ChatMessage cm = ChatMessage.builder().key(key).authorId(sender.getUsername()).seeners(seeners).text(text).build();


        return chatMessageRepository.save(cm);
    }

    private Conversation updateOrderedConversations(String conversationId, long thisTime) {
        log.info("==============updateOrderedConversations==============");
        Conversation userCon = conRepo.findById(conversationId).orElseThrow(() -> new NullPointerException("Conversation not found"));

        Set<Member> members = userCon.getMembers();
        List<String> userIds = members.stream().map(e -> e.getUserId()).collect(Collectors.toList());
        List<OrderedConversation> orderedCons = new ArrayList<>();

        for (String userId : userIds) {
            OrderedConversation o1 = OrderedConversation.builder().conversationId(conversationId).lastMessageTime(thisTime).userId(userId)

                    .build();

            orderedCons.add(o1);
        }


        orderedConverRepo.saveAll(orderedCons);

        orderedConverRepo.deleteAllByIdsAndLastMessageTime(userIds, userCon.getLastMessageTime());

        conRepo.updateLastMessageTime(conversationId, thisTime);

        return userCon;

    }

    private void noticeMessageViaSocket(List<String> userIds, ChatMessage cm) {

        List<User> users = userRepo.findAllById(userIds);

        List<UserDto> collect = users.stream().map(e -> new UserDto(e)).collect(Collectors.toList());


        for (User user : users) {
            if (user != null && user.getSocketId() != null) {
                SocketIOClient client1 = server.getClient(UUID.fromString(user.getSocketId()));
                if (client1 != null) {
                    client1.sendEvent("message", cm, collect);
                }
            }
        }


    }


    @Override
    public List<Conversation> getAllConversation(String userId, Pageable pageable) {


        Slice<OrderedConversation> userCons = orderedConverRepo.findAllByUserId(userId, pageable);

        List<String> cons = userCons.stream().map(e -> e.getConversationId()).collect(Collectors.toList());

        List<Conversation> result = conRepo.findAllById(cons);

        return result;

    }

    @Override
    public List<ChatMessage> getAllChatMessageInConversation(String conversationId, int page, int size) {

        Slice<ChatMessage> slide = chatMessageRepository.findAllByConversationId(conversationId, Pageable.ofSize(page * size));

        List<ChatMessage> result = slide.stream().skip((page - 1) * size).collect(Collectors.toList());

        return result;
    }


    @Override
    public void seenAMessage(User seenBy, Conversation con) {

        ChatMessage lastMessage = chatMessageRepository.findOneById(con.getConversationId(), con.getLastMessageTime()).orElseThrow(() -> new NullPointerException("ChatMessage not found"));

        Seener seener = Seener.builder().username(seenBy.getUsername()).seenAt(System.currentTimeMillis()).build();

        Set<Seener> seeners = lastMessage.getSeeners();

        Optional<Seener> any = seeners.stream().filter(e -> e.getUsername().equalsIgnoreCase(seenBy.getUsername())).findAny();

        if (any.isPresent()) return;

        seeners.add(seener);

        chatMessageRepository.save(lastMessage);

        // notice socket
        Set<Member> members = con.getMembers();

        List<User> users = userRepo.findAllById(members.stream().map(e -> e.getUserId()).collect(Collectors.toList()));

        for (User user : users) {
            if (user.getSocketId() != null) {
                SocketIOClient client = server.getClient(UUID.fromString(user.getSocketId()));
                if (client != null) {
                    client.sendEvent("seen", lastMessage);
                }
            }
        }

    }


    @Override
    public List<ChatMessage> findAllLastMessage(List<Conversation> conversations) {


        List<String> ids = conversations.stream().map(e -> e.getConversationId()).collect(Collectors.toList());

        List<Long> lastMessTimes = conversations.stream().map(e -> e.getLastMessageTime()).collect(Collectors.toList());


        return chatMessageRepository.findAllLastMessage(ids, lastMessTimes);

    }

    @Override
    public List<User> getAllUserByConversations(List<Conversation> convs) {

        List<String> userIds = convs.stream().map(e -> e.getMembers()).flatMap(Set::stream).map(e -> e.getUserId()).collect(Collectors.toList());

        List<User> users = userRepo.findAllById(userIds);

        return users;
    }

    @Override
    public String createNewConversation(User creator, User invitee) {
        String converId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Member m0 = new Member(creator.getUsername(), invitee.getUsername());
        Member m1 = new Member(invitee.getUsername(), creator.getUsername());

        Set<Member> mems = new HashSet<>();
        mems.add(m0);
        mems.add(m1);

        Conversation uc0 = Conversation.builder().conversationId(converId).lastMessageTime(now).members(mems).build();

        conRepo.save(uc0);

        OrderedConversation con0 = OrderedConversation.builder().lastMessageTime(now).userId(creator.getUsername()).conversationId(converId).build();

        OrderedConversation con1 = OrderedConversation.builder().lastMessageTime(now).userId(invitee.getUsername()).conversationId(converId).build();


        orderedConverRepo.saveAll(Arrays.asList(con0, con1));

        this.sendMessage(creator, converId, creator.getFullName() + " create a conversation with " + invitee.getFullName(), now + 1);

        return converId;
    }


}
