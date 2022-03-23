package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.Member;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import com.bobvu.tinherbackend.cassandra.repository.ChatMessageRepository;
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
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserConversationRepository userConversationRepository;
    @Override
    public void createNewConversation(User creator, String conversationName) {
        Member mem = Member.builder()
                .userId(creator.getId())
                .username(creator.getUsername())
                .memberShipStatus("Creator")
                .build();




        sendMessage(creator,"CONVER-" + UUID.randomUUID(), conversationName,System.currentTimeMillis(),creator.getUsername() + " created a new conversation name " + conversationName, "System",Arrays.asList(mem ));


    }

    public void sendMessage(User sender, String conversationId,String conversationName,Long lastMessageAt,  String lastMessageText, String lastMessageSender,List<Member> members){
        long thisTime = System.currentTimeMillis();


        // save new chat message
        ChatMessage cm = ChatMessage.builder()
                .sentAt(thisTime)
                .conversationId(conversationId)
                .author(sender.getUsername())
                .text(lastMessageText)
                .build();


        chatMessageRepository.save(cm);


        // save new Conversation


        List<UserConversation> userCons = new ArrayList<>();


        for(Member mem : members){
            UserConversation uc = UserConversation.builder()
                    .clusterKey(conversationId + "-" + System.currentTimeMillis())
                    .userId(mem.getUserId())
                    .conversationId(conversationId)
                    .createTime(System.currentTimeMillis())
                    .lastMessageText(lastMessageText)
                    .lastMessageAt(lastMessageAt)
                    .lastMessageSender(lastMessageSender)
                    .conversationName(conversationName)
                    .build();

            userCons.add(uc);
        }
        userConversationRepository.saveAll(userCons);

        // delete all old conversation
        List<String> collect = members.stream().map(e -> e.getUserId()).collect(Collectors.toList());

        String minConId = conversationId + "-0000000000000";

        String maxConId = conversationId + "-" + thisTime;

        userConversationRepository.deleteAllByIds(collect, minConId, maxConId);



    }





    @Override
    public void inviteUserToConversation(User inviter, User invitee, String  clusterKey) {

        UserConversation ucon = userConversationRepository.findOneByUserIdAndClusterKey(inviter.getId(), clusterKey);
        List<Member> members = new ArrayList<>(ucon.getMembers());

        Member newMem = Member.builder().userId(invitee.getId())
                .username(invitee.getUsername())
                .memberShipStatus("Member").build();
        members.add(newMem);


        long thisTime = System.currentTimeMillis();
        sendMessage(inviter, ucon.getConversationId(), ucon.getConversationName(),thisTime, invitee.getUsername() + " invited by " + inviter.getUsername(), "System", members );


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
    public void seenAMessage(User seenBy, ChatMessage chatMessage) {

    }
}
