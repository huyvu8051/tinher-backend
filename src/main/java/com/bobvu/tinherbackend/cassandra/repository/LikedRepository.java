package com.bobvu.tinherbackend.cassandra.repository;

import com.bobvu.tinherbackend.cassandra.model.Liked;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikedRepository extends CassandraRepository<Liked, String> {

    @Query("SELECT * FROM liked WHERE username = :username and likedTargetId = :partnerId")
    Optional<Liked> findOneById(@Param("username") String username, @Param("partnerId") String partnerId);

    @Query("SELECT * FROM liked WHERE username = :username")
    List<Liked> findAllLikedTargetId(@Param("username") String username);
}
