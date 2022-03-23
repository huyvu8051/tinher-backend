package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.Conversation;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ConversationRepository extends CassandraRepository<Conversation, String> {
}
