package com.bobvu.tinherbackend.match;

import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.chat.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("user/match")
public class MatchController {
private final MatchService matchService;
private final ChatService chatService;


    @PostMapping
    public ResponseEntity<PageResponse<ProfileResponse>> findAllSuitablePerson(@RequestBody FindSuitablePersonRequest request){
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(matchService.findAllSuitablePerson(userDetails, request));
    }

    @PostMapping("/like")
    public ResponseEntity findAllSuitablePerson(@RequestBody LikePartnerRequest request){
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        matchService.likePartner(userDetails, request);
        return  ResponseEntity.ok().build();
    }
}