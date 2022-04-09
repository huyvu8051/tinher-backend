package com.bobvu.tinherbackend.socket;


import com.bobvu.tinherbackend.auth.JwtUtil;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class MessageEventHandler {
    private final SocketIOServer server;

    private final UserRepository userrepo;


    private final JwtUtil jwtUtil;


    @OnConnect
    public void onConnect(SocketIOClient client) {
        client.sendEvent("connected", "connect success");
        log.info("a client was connected");
    }


    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {

        client.disconnect();
        log.info("a client was disconnected");

    }

    @OnEvent("clientConnectRequest")
    public void clientConnectRequest(SocketIOClient client, SocketAuthorizationRequest request) throws Exception {
        try {

            // authorization
            String token = request.getToken().substring(7);

            String id = jwtUtil.extractUsername(token);

            User usr = userrepo.findById(id).orElseThrow(() -> new NullPointerException("User not found"));

            if (!jwtUtil.validateToken(token, usr)) {
                throw new Exception("Bad token");
            }

            usr.setSocketId(client.getSessionId().toString());

            userrepo.save(usr);

            client.sendEvent("registerSuccess", "connect success");

            // missing set security context holder


            log.info("Client connect socket success!");
        } catch (Exception e) {
            log.error(e.getMessage());
            client.sendEvent("joinError", "joinError");
            client.disconnect();
        }

    }


}