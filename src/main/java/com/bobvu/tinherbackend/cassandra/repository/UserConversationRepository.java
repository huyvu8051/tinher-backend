package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserConversationRepository extends CassandraRepository<UserConversation, String> {
    Optional<UserConversation> findOneByUserIdAndConversationId(String userId, String conversationId);

    @Query("SELECT * FROM userConversation WHERE userId in :userIds and conversationId = :conversationId")
    List<UserConversation> findAllByUserIdsAndConverId(@Param("userIds") List<String> userIds, @Param("conversationId") String converId);

    @Query("SELECT * FROM userConversation WHERE userId = :userId and conversationId in :converIds")
    List<UserConversation> findAllByUserIdAndConversationIds(@Param("userId") String userId, @Param("converIds") List<String> cons);

    @Query("UPDATE userConversation SET lastMessageTime = :time WHERE userId in :userIds AND conversationId =: converId")
    void updateLastMessageTime(@Param("userIds") List<String> asList, @Param("converId") String converId, @Param("time") long time);
}
