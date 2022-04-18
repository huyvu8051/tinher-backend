package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private ChatService chatSer;

    @GetMapping("/chatMessage")
    public  ChatMessageInfo getAllChatMessage (@RequestParam String convId, @RequestParam int page, @RequestParam int size){

        int limit = page * 10;

        List<ChatMessage> chatMess = chatSer.getAllChatMessageInConversation(convId, page, size);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserConversation con = chatSer.findConversationById(user.getUsername(), convId);


        List<String> memberIds = con.getMemberIds();

        List<UserAvatarUrl> userAvatarUrls = chatSer.getUserAvatarUrls(memberIds);

        return ChatMessageInfo.builder()
                .chatMessages(chatMess)
                .conversation(con)
                .avatarUrls(userAvatarUrls)
                .build();
    }

    @GetMapping("/conversations")
    public  GetListConverRes getAllConversations (){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserConversation> allConversation = chatSer.getAllConversation(user.getUsername(), PageRequest.of(0, 10));
        return GetListConverRes.builder()
                .conversations(allConversation)
                .build();

    }


    @PostMapping("/chatMessage")
    public void sentMessage(@RequestBody SentMessageReq req){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        chatSer.sendMessage(user, req.getConversationId(), req.getChatMessage(), req.getSentAt());
    }





}
