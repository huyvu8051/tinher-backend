package com.bobvu.tinherbackend.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @GetMapping
    public String home (){
        return "hello chat ";
    }
}
