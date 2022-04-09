package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.Conversation;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import com.datastax.oss.driver.api.core.cql.PagingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private ChatService chatSer;

    @GetMapping("/chatMessage")
    public  ChatMessageInfo getAllChatMessage (@RequestParam String convId, @RequestParam int page){

        int limit = page * 10;

        List<ChatMessage> chatMess = chatSer.getAllChatMessageInConversation(convId, Pageable.ofSize(limit));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserConversation con = chatSer.findConversationById(user.getId(), convId);
        return ChatMessageInfo.builder()
                .chatMessages(chatMess)
                .conversation(con)
                .build();
    }

    @GetMapping("/conversations")
    public  GetListConverRes getAllConversations (){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserConversation> allConversation = chatSer.getAllConversation(user.getId(), PageRequest.of(0, 10));
        return GetListConverRes.builder()
                .conversations(allConversation)
                .build();

    }


    @PostMapping("/chatMessage")
    public void sentMessage(@RequestBody SentMessageReq req){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        chatSer.sendMessage(user, req.getConversationId(), req.getChatMessage());
    }


}
