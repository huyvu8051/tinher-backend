package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends CassandraRepository<ChatMessage, String> {
    @Query("SELECT * FROM chatMessage WHERE conversationId = :converId")
    Slice<ChatMessage> findAllByConversationId(@Param("converId") String converId, Pageable pageable);
}
