package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.Conversation;
import com.bobvu.tinherbackend.cassandra.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private ChatService chatSer;

    @GetMapping("/chatMessage")
    public ChatMessageInfo getAllChatMessage( @RequestParam String convId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Conversation con = chatSer.findConversationById(user.getUsername(), convId);
        chatSer.seenAMessage(user, con);
        List<ChatMessage> chatMess = chatSer.getAllChatMessageInConversation(convId, page, size);

        List<User> users = chatSer.getAllUserByConversations(Arrays.asList(con));

        return ChatMessageInfo.builder()
                .chatMessages(chatMess)
                .conversation(con)
                .users(users)
                .build();
    }

    @GetMapping("/conversations")
    public GetListConverRes getAllConversations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Conversation> allConversation = chatSer.getAllConversation(user.getUsername(), PageRequest.of(0, 10));

        List<String> partnerIds = allConversation.stream().map(e -> e.getMembers()).flatMap(Set::stream).map(e -> e.getUserId()).collect(Collectors.toList());

        List<User> users = chatSer.getAllUserByConversations(allConversation);

        List<ChatMessage> lastMessages = chatSer.findAllLastMessage(allConversation);

        return GetListConverRes.builder()
                .conversations(allConversation)
                .users(users)
                .lastMessages(lastMessages)
                .build();

    }


    @PostMapping("/chatMessage")
    public void sentMessage(@RequestBody SentMessageReq req) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        chatSer.sendMessage(user, req.getConversationId(), req.getChatMessage(), req.getSentAt());
    }
}
