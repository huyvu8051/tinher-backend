package com.bobvu.tinherbackend;

import com.bobvu.tinherbackend.cassandra.config.DataStaxAstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class TinherBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinherBackendApplication.class, args);
	}

}
