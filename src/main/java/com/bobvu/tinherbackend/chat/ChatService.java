package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
    Conversation findConversationById(String userId, String conversationId);




    /**
     *  get all conversation
     *
     * @param userId
     * @param pageable
     * @return
     */


    List<Conversation> getAllConversation(String userId, Pageable pageable);

    /** get all chat message in conversation
     *

     * @return
     */
    List<ChatMessage> getAllChatMessageInConversation(String conversationId, int page, int size);



    void sendMessage(User sender, String conversationId,  String text, long sendAt);

    /**
     * seen a message in a conversation then everyone in conversation can know who has already read that message
     *
     * @param sentAt
     */
    void seenAMessage(User seenBy,String convId, long sentAt);


    String createNewConversation(User userDetails, User members);

    List<ChatMessage> findAllLastMessage(List<Conversation> orderedConvers);

    List<User> getAllUserByConversations(List<Conversation> conversations);
}
