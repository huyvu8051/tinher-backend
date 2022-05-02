package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.OrderedConversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderedConversationRepository extends CassandraRepository<OrderedConversation, String> {
    @Query("SELECT * FROM orderedConversation WHERE userId = :userId")
    Slice<OrderedConversation> findAllByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("DELETE FROM orderedConversation WHERE userId in :userIds AND lastMessageTime = :lastMessageTime")
    void deleteAllByIdsAndLastMessageTime(@Param("userIds") List<String> userIds, @Param("lastMessageTime") long lastMessageTime);

    Optional<OrderedConversation> findOneByUserIdAndLastMessageTime(String userId, long lastMessageTime);

    @Query("SELECT * FROM orderedConversation WHERE userId in :userIds and lastMessageTime = :lastMessageTime")
    List<OrderedConversation> findAllByUserIdsAndLastMessageTime(@Param("userIds")List<String> userIds, @Param("lastMessageTime") long lastMessageTime);
}
