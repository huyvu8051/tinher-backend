package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.Conversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends CassandraRepository<Conversation, String> {
    List<Conversation> findAllByUserId(String userId);

    @Query("DELETE FROM conversation WHERE userId in :userIds AND lastMessageTime = :lastMessageTime")
    void deleteAllByIdsAndLastMessageTime(@Param("userIds") List<String> userIds, @Param("lastMessageTime") long lastMessageTime);

    Conversation findOneByUserIdAndLastMessageTime(String userId, long lastMessageTime);

    @Query("SELECT * FROM conversation WHERE userId in :userIds and lastMessageTime = :lastMessageTime")
    List<Conversation> findAllByUserIdsAndLastMessageTime(@Param("userIds")List<String> userIds,@Param("lastMessageTime") long lastMessageTime);
}
