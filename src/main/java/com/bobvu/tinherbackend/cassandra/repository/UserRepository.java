package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserRepository extends CassandraRepository<User, String> {
}
