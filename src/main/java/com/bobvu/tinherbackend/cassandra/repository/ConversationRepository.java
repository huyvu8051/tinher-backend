package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.Conversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends CassandraRepository<Conversation, String> {

    @Query("UPDATE conversation SET lastMessageTime = :time WHERE conversationId = :converId")
    void updateLastMessageTime( @Param("converId") String converId, @Param("time") long time);
}
