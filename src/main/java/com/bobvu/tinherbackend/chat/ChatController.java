package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.CMKey;
import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private ChatService chatSer;

    @GetMapping("/chatMessage")
    public ChatMessageInfo getAllChatMessage( @RequestParam String convId, @RequestParam int page, @RequestParam int size) {

        int limit = page * 10;

        List<ChatMessage> chatMess = chatSer.getAllChatMessageInConversation(convId, page, size);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserConversation con = chatSer.findConversationById(user.getUsername(), convId);


        List<User> users = chatSer.getAllUserByConversationIds(Arrays.asList(con.getConversationId()));


        return ChatMessageInfo.builder()
                .chatMessages(chatMess)
                .conversation(con)
                .users(users)
                .build();
    }

    @GetMapping("/conversations")
    public GetListConverRes getAllConversations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserConversation> allConversation = chatSer.getAllConversation(user.getUsername(), PageRequest.of(0, 10));

        List<String> partnerIds = allConversation.stream().map(e -> e.getPartnerId()).collect(Collectors.toList());

        List<String> converIds = allConversation.stream().map(e -> e.getConversationId()).collect(Collectors.toList());
        List<User> users = chatSer.getAllUserByConversationIds(partnerIds);

        List<ChatMessage> lastMessages = chatSer.findAllLastMessage(converIds);

        return GetListConverRes.builder()
                .conversations(allConversation)
                .users(users)
                .lastMessages(lastMessages)
                .build();

    }


    @PostMapping("/chatMessage")
    public void sentMessage(@RequestBody SentMessageReq req) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        chatSer.sendMessage(user, req.getConversationId(), req.getChatMessage(), System.currentTimeMillis());
    }

    @PostMapping("/seenMessage")
    public void seenMessage(@RequestBody SeenMessageReq req) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        chatSer.seenAMessage(user,req.getConverId(), req.getChatMessageId());
    }
}
