package com.bobvu.tinherbackend.match;

import com.bobvu.tinherbackend.cassandra.mapper.UserMapper;
import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Liked;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.repository.LikedRepository;
import com.bobvu.tinherbackend.cassandra.repository.ConversationRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.bobvu.tinherbackend.chat.ChatService;
import com.bobvu.tinherbackend.elasticsearch.User;
import com.bobvu.tinherbackend.elasticsearch.UserESRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MatchServiceImpl implements MatchService {
    private final UserESRepository userESRepository;

    private final UserMapper userMapper;
    private final ElasticsearchOperations esOperations;
    private final UserRepository userRepository;

    private final LikedRepository likedRepo;

    private final ChatService chatSer;


    @Override
    public PageResponse<com.bobvu.tinherbackend.cassandra.model.User> findAllSuitablePerson(com.bobvu.tinherbackend.cassandra.model.User user, FindSuitablePersonRequest request) {

        GeoDistanceQueryBuilder geoDistanceQueryBuilder = new GeoDistanceQueryBuilder("location").distance(user.getDistancePreference() + "", DistanceUnit.KILOMETERS).point(request.getLat(), request.getLon());

        int year = Calendar.getInstance().get(Calendar.YEAR);

        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("yearOfBirth").gte(year - user.getMaxAge()).lte(year - user.getMinAge());


        BoolQueryBuilder genderBoolQuery = new BoolQueryBuilder();

        if (user.getLookingFor() != null) {
            for (Gender gender : user.getLookingFor()) {
                genderBoolQuery.should(new MatchQueryBuilder("gender", gender));
            }
        }

        List<Liked> likeds = likedRepo.findAllLikedTargetId(user.getUsername());

        List<String> ignoreIds = likeds.stream().map(e -> e.getLikedTargetId()).collect(Collectors.toList());

        ignoreIds.add(user.getUsername());

        QueryBuilder mnTerms = new TermsQueryBuilder("_id", ignoreIds);
        BoolQueryBuilder mustNotInIds = new BoolQueryBuilder();
        mustNotInIds.mustNot(mnTerms);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                .filter(geoDistanceQueryBuilder)
                .filter(genderBoolQuery)
                .filter(rangeQueryBuilder)
                .filter(mustNotInIds)
                .minimumShouldMatch(1);

        for (Passion passion : user.getPassions()) {
            boolQueryBuilder.should(new MatchQueryBuilder("passions", passion));
        }


        // random score
        Script script = new Script("if(System.currentTimeMillis() <= doc['boostTime'].value)return 1 + Math.random() ;else return Math.random();");

        ScriptScoreQueryBuilder randomQuery = new ScriptScoreQueryBuilder(boolQueryBuilder, script);

        Pageable pageable = Pageable.ofSize(7);

        Query searchQuery = new NativeSearchQueryBuilder().withQuery(randomQuery).build().setPageable(pageable);

        SearchHits<User> searchHits = esOperations.search(searchQuery, User.class, IndexCoordinates.of("user"));

        long totalHits = searchHits.getTotalHits();

        List<String> ids = searchHits.get().map(e -> e.getContent().getUsername()).collect(Collectors.toList());

        List<com.bobvu.tinherbackend.cassandra.model.User> users = userRepository.findAllById(ids);

        PageResponse<com.bobvu.tinherbackend.cassandra.model.User> response = new PageResponse<>();
        response.setList(users);
        response.setTotalElement(totalHits);

        return response;


    }

    @Autowired
    private ConversationRepository userConRepo;

    @Override
    public void likePartner(com.bobvu.tinherbackend.cassandra.model.User userDetails, LikePartnerRequest request) {

        if(userDetails.getUsername().equalsIgnoreCase(request.getPartnerId())){
            return;
        }

        boolean present = likedRepo.findOneById(request.getPartnerId(), userDetails.getUsername()).isPresent();

        if(!present){
            this.createLikedPartner(userDetails.getUsername(), request.getPartnerId());



        }else {
            this.createLikedPartner( request.getPartnerId(), userDetails.getUsername());
            this.pairAndCreateConversation(userDetails, request.getPartnerId());
        }
    }

    final long THIRTY_MINUTE = 30 * 60 * 1000;

    @Override
    public void boost(com.bobvu.tinherbackend.cassandra.model.User userDetails) {
        long now = System.currentTimeMillis();

        User u = userESRepository.findById(userDetails.getUsername()).orElseThrow(() -> new NullPointerException("User not found"));
        u.setBoostTime(now + THIRTY_MINUTE);

        userESRepository.save(u);

        userDetails.setBoostTime(now + THIRTY_MINUTE);

        userRepository.save(userDetails);



    }

    private void createLikedPartner(String userId, String partnerId) {
        Liked l = Liked.builder()
                .username(userId)
                .likedTargetId(partnerId)
                .createTime(System.currentTimeMillis())
                .build();

        likedRepo.save(l);



    }

    private void pairAndCreateConversation(com.bobvu.tinherbackend.cassandra.model.User userDetails, String partnerId) {
        com.bobvu.tinherbackend.cassandra.model.User partner = userRepository.findById(partnerId).orElseThrow(() -> new NullPointerException("Username not found"));

        String converId2 = chatSer.createNewConversation(userDetails, partner);
        
    }


}