package com.bobvu.tinherbackend.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserESRepository extends ElasticsearchRepository<User, String> {

}