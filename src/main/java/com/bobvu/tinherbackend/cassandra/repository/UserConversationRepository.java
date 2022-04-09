package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.ChatMessageType;
import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserConversationRepository extends CassandraRepository<UserConversation, String> {
    UserConversation findOneByUserIdAndConversationId(String userId, String conversationId);

    @Query("SELECT * FROM userConversation WHERE userId in :userIds and conversationId = :conversationId")
    List<UserConversation> findAllByUserIdsAndConverId(@Param("userIds") List<String> userIds, @Param("conversationId") String converId);

    @Query("UPDATE userConversation set lastMessageTime = :lastMessageTime, lastMessage = :lastMessage WHERE userId in :userIds and conversationId = :conversationId")
    void updateLastMessageTime(@Param("userIds") List<String> userIds, @Param("conversationId") String conversationId, @Param("lastMessageTime") long lastMessageTime, @Param("lastMessage")ChatMessageType cmt);

    @Query("SELECT * FROM userConversation WHERE userId = :userId and conversationId in :converIds")
    List<UserConversation> findAllByUserIdAndConversationIds(@Param("userId") String userId, @Param("converIds") List<String> cons);
}
