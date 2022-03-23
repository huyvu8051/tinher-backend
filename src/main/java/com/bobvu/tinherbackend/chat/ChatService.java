package com.bobvu.tinherbackend.chat;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import com.bobvu.tinherbackend.cassandra.model.Conversation;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {


    Conversation createNewConversation(User creator, String conversationName);

    Conversation inviteUserToConversation(User inviter, User invitee, Conversation conversation);

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

    /**
     * @param sender
     * @param text
     * @param conversation
     */
    void sendAMessageToAConversation(User sender, String text, Conversation conversation);

    /**
     * seen a message in a conversation then everyone in conversation can know who has already read that message
     *
     * @param chatMessage
     */
    void seenAMessage(User seenBy, ChatMessage chatMessage);
}
