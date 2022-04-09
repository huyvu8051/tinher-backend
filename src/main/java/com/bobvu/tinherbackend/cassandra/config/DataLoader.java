package com.bobvu.tinherbackend.cassandra.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private SocketIOServer server;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try {
            server.start();
            log.info("Socket launch successful!");
        } catch (Exception e) {
            log.error("Socket launch failure!");
            log.error(e.getMessage());
        }




    }


}
