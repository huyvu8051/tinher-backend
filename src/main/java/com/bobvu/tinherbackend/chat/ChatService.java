package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
    UserConversation findConversationById(String userId, String conversationId);

    String createNewConversation(User creator, String conversationName);

    void inviteUserToConversation(User inviter, User invitee, String clusterKey);

    /**
     *  get all conversation
     *
     * @param userId
     * @param pageable
     * @return
     */


    List<UserConversation> getAllConversation(String userId, Pageable pageable);

    /** get all chat message in conversation
     *
     * @param conversationId
     * @param pageable
     * @return
     */
    List<ChatMessage> getAllChatMessageInConversation(String conversationId, Pageable pageable);



    void sendMessage(User sender, String conversationId,  String text);

    /**
     * seen a message in a conversation then everyone in conversation can know who has already read that message
     *
     * @param chatMessage
     */
    void seenAMessage(User seenBy, ChatMessage chatMessage);

}
