package com.bobvu.tinherbackend.auth.google;

import com.bobvu.tinherbackend.auth.JwtUtil;
import com.bobvu.tinherbackend.auth.UserService;
import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.repository.ConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.bobvu.tinherbackend.chat.ChatService;
import com.github.javafaker.Faker;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.message.AuthException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("authentication")
@AllArgsConstructor
public class GoogleAuthenticationController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userSer;


    @Autowired
    private ConversationRepository userConversationRepository;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;


    @PostMapping("/google/login")
    public AuthenticationResponse authenticate(@RequestBody GoogleAuthenticationRequest request) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())

                .build();


        // (Receive idTokenString by HTTPS POST)

        String idToken1 = request.getIdToken();
        GoogleIdToken idToken = verifier.verify(idToken1);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            if (!emailVerified) {
                throw new AuthException("Email not verified!");
            }
            String email = payload.getEmail();


            User user;
            try {
                Optional<User> optional = userRepository.findById(email);
                user = optional.orElseThrow(() -> new UsernameNotFoundException("Account not registered!"));


            } catch (UsernameNotFoundException e) {

                // create new account
                String userId = payload.getSubject();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");// fullname

                // Use or store profile information
                // ...



                Random rand = new Random();




                user =  userSer.createNewUser(email, name, pictureUrl);

            }

            userSer.updateUserLocation(request.getLat(), request.getLon(), email);

            String token = jwtUtil.generateToken(user);

            return AuthenticationResponse.builder()
                    .jwt(token)
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .avatar(user.getAvatar())
                    .authorities(user.getAuthorities())
                    .build();
        } else {
            //System.out.println("Invalid ID token.");
            log.error("Invalid token id!");
            throw new AuthException("Token id not valid!");
        }
    }


}