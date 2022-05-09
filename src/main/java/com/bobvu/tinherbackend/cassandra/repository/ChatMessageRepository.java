package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.CMKey;
import com.bobvu.tinherbackend.cassandra.model.ChatMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends CassandraRepository<ChatMessage, CMKey> {
    @Query("SELECT * FROM chatMessage WHERE conversationId = :converId")
    Slice<ChatMessage> findAllByConversationId(@Param("converId") String converId, Pageable pageable);

    @Query("SELECT * FROM chatMessage WHERE conversationId = :converId AND lastMessageTime = :sentAt")
    Optional<ChatMessage> findOneById(@Param("converId") String convId, @Param("sentAt") long cmId);

    @Query("SELECT * FROM chatMessage WHERE conversationId in :converIds and lastMessageTime in :lmts")
    List<ChatMessage> findAllLastMessage(@Param("converIds") List<String> ids,@Param("lmts") List<Long> lastMessTimes);

}
