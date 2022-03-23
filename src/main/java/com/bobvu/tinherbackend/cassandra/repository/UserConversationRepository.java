package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.UserConversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserConversationRepository extends CassandraRepository<UserConversation, String> {
    List<UserConversation> findAllByUserId(String userId);

    @Query("DELETE FROM userConversation WHERE userId in :userIds AND clusterKey > :minConId AND clusterKey < :maxConId")
    void deleteAllByIds(@Param("userIds") List<String> userIds, @Param("minConId") String minConId, @Param("maxConId") String maxConId);
}
