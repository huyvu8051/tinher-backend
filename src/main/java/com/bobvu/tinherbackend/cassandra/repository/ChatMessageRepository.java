package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatMessageRepository extends CassandraRepository<ChatMessage, String> {
    List<ChatMessage> findAllByConversationId(String targetId, Pageable pageable);
}
