package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.Conversation;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetListConverRes {
    private List<UserConversation> conversations;
    private int page;
    private int size;
}
