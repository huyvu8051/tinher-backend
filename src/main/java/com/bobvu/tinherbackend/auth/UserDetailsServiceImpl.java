package com.bobvu.tinherbackend.auth;

import com.bobvu.tinherbackend.cassandra.mapper.UserMapper;
import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Image;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.bobvu.tinherbackend.elasticsearch.UserESRepository;
import com.github.javafaker.Faker;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserESRepository userEsRepo;

    @Autowired
    private UserMapper userMap;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() -> new NullPointerException("Username not found"));

        return user;

    }

    @Override
    public User createNewUser(String email, String fullname, String avatarUrl) {
        Random rand = new Random();

        Faker faker = new Faker(new Locale("vi-VN"));

        User user = User.builder().username(email).fullName(fullname).avatar(avatarUrl)

                .images(Arrays.asList(new Image(avatarUrl), new Image(getRandomImgUrl()), new Image(getRandomImgUrl()), new Image(getRandomImgUrl()), new Image(getRandomImgUrl()), new Image(getRandomImgUrl())))
                .lat(10.8634736 + rand.nextFloat())
                .lon(106.7350396 + rand.nextFloat())
                .distancePreference(5000)
                .passions(Arrays.asList(Passion.Astrology, Passion.DIY, Passion.Climbing, Passion.getRandom()))
                .about(faker.lorem().sentence())
                .roles(Arrays.asList("user"))
                .socketId(UUID.randomUUID().toString())
                .maxAge(70)
                .minAge(15)
                .gender(Gender.getRandom())
                .lookingFor(Arrays.asList(Gender.FEMALE, Gender.MALE))
                .dateOfBirth(LocalDate.of(1962 + rand.nextInt(40), rand.nextInt(11) +1, rand.nextInt(26) + 1))
                .build();


        com.bobvu.tinherbackend.elasticsearch.User userEs = userMap.toEsEntity(user);

        userEsRepo.save(userEs);


        return userRepository.save(user);
    }

    @Override
    public void updateUserLocation(double lat, double lon, String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new NullPointerException("Username not found"));
        user.setLon(lon);
        user.setLat(lat);
        userRepository.save(user);

        com.bobvu.tinherbackend.elasticsearch.User userEs = userEsRepo.findById(username).orElseThrow(() -> new NullPointerException("Username not found"));

        GeoPoint geoPoint = new GeoPoint(lat, lon);

        userEs.setLocation(geoPoint);
        userEsRepo.save(userEs);

    }

    private String getRandomImgUrl() {
        List<String> imgUrls = new ArrayList<>();

        imgUrls.add("https://i.imgur.com/lpzlDQv.jpg");
        imgUrls.add("https://i.imgur.com/pAZ8UUQ.jpg");
        imgUrls.add("https://i.imgur.com/qfLln70.jpg");
        imgUrls.add("https://i.pinimg.com/564x/ba/ef/8c/baef8c84567c3ebadce92439a04bd387.jpg");
        imgUrls.add("https://i.pinimg.com/564x/8b/f5/62/8bf5626b6ac0d1be07fd63d0ad413012.jpg");
        imgUrls.add("https://i.pinimg.com/564x/61/e8/e7/61e8e7e634c7cf80dc255c93578ea56c.jpg");
        imgUrls.add("https://i.pinimg.com/564x/eb/56/11/eb5611b3a7e67fab261dee88df25b19a.jpg");
        imgUrls.add("https://i.pinimg.com/564x/80/3a/4e/803a4ecc6a5eb15a165218d481b3e077.jpg");
        imgUrls.add("https://i.pinimg.com/originals/f3/60/31/f36031f081af4dcfd7f25476c7e9f56b.jpg");
        imgUrls.add("https://i.pinimg.com/564x/9d/69/1d/9d691dc8d36789197f69431fadfb77e7.jpg");
        imgUrls.add("https://i.pinimg.com/564x/4a/5e/7d/4a5e7deed283397043b3c394fb00f565.jpg");
        imgUrls.add("https://i.pinimg.com/564x/5d/a2/70/5da27003cba271fe295a787ea3fba410.jpg");

        Random random = new Random();
        int rand = random.nextInt(6);

        return imgUrls.get(rand);
    }
}
