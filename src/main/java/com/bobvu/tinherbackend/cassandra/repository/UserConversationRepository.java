package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.Conversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserConversationRepository extends CassandraRepository<Conversation, String> {
    List<Conversation> findAllByUserId(String userId);

    @Query("DELETE FROM userConversation WHERE userId in :userIds AND createTime = :createTime AND conversationId = :conversationId")
    void deleteAllByIds(@Param("userIds") List<String> userIds, @Param("createTime") long createTime, @Param("conversationId") String conversationId);

    Conversation findOneByUserIdAndConversationIdAndCreateTime(String userId, String clusterKey, long createTime);
}
