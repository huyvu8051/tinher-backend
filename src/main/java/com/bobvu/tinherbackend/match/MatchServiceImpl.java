package com.bobvu.tinherbackend.match;

import com.bobvu.tinherbackend.cassandra.mapper.UserMapper;
import com.bobvu.tinherbackend.cassandra.model.Gender;
import com.bobvu.tinherbackend.cassandra.model.Liked;
import com.bobvu.tinherbackend.cassandra.model.Passion;
import com.bobvu.tinherbackend.cassandra.repository.LikedRepository;
import com.bobvu.tinherbackend.cassandra.repository.UserRepository;
import com.bobvu.tinherbackend.chat.ChatService;
import com.bobvu.tinherbackend.elasticsearch.User;
import com.bobvu.tinherbackend.elasticsearch.UserESRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
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
    public PageResponse<ProfileResponse> findAllSuitablePerson(com.bobvu.tinherbackend.cassandra.model.User user, FindSuitablePersonRequest request) {

        GeoDistanceQueryBuilder geoDistanceQueryBuilder = new GeoDistanceQueryBuilder("location").distance(user.getDistancePreference() + "", DistanceUnit.KILOMETERS).point(request.getLat(), request.getLon());

        int year = Calendar.getInstance().get(Calendar.YEAR);

        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("yearOfBirth").gte(year - user.getMaxAge()).lte(year - user.getMinAge());


        BoolQueryBuilder boolQueryBuilderGender = new BoolQueryBuilder();

        if (user.getLookingFor() != null) {
            for (Gender gender : user.getLookingFor()) {
                boolQueryBuilderGender.should(new MatchQueryBuilder("gender", gender));
            }
        }

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                .filter(geoDistanceQueryBuilder)
                .filter(boolQueryBuilderGender)
                .filter(rangeQueryBuilder)
                .minimumShouldMatch(1);

        for (Passion passion : user.getPassions()) {
            boolQueryBuilder.should(new MatchQueryBuilder("passions", passion));
        }

        // random score
        Script script = new Script("Math.random()");

        ScriptScoreQueryBuilder randomQuery = new ScriptScoreQueryBuilder(boolQueryBuilder, script);


        Pageable pageable = Pageable.ofSize(5);


        Query searchQuery = new NativeSearchQueryBuilder().withQuery(randomQuery).build().setPageable(pageable);

        SearchHits<User> searchHits = esOperations.search(searchQuery, User.class, IndexCoordinates.of("user"));

        long totalHits = searchHits.getTotalHits();
        List<ProfileResponse> collect = searchHits.get().map(e -> userMapper.toProfileResponse(e.getContent())).collect(Collectors.toList());

        PageResponse<ProfileResponse> response = new PageResponse<>();
        response.setList(collect);
        response.setTotalElement(totalHits);

        return response;


    }

    @Override
    public void likePartner(com.bobvu.tinherbackend.cassandra.model.User userDetails, LikePartnerRequest request) {
        boolean present = likedRepo.findOneById(request.getPartnerId(), userDetails.getUsername()).isPresent();

        if(present){
            this.pairAndCreateConversation(userDetails, request.getPartnerId());
        }else {
            this.createLikedPartner(userDetails, request.getPartnerId());
        }
    }

    private void createLikedPartner(com.bobvu.tinherbackend.cassandra.model.User userDetails, String partnerId) {
        Liked l = Liked.builder()
                .username(userDetails.getUsername())
                .likedTargetId(partnerId)
                .build();

        likedRepo.save(l);



    }

    private void pairAndCreateConversation(com.bobvu.tinherbackend.cassandra.model.User userDetails, String partnerId) {
        com.bobvu.tinherbackend.cassandra.model.User partner = userRepository.findById(partnerId).orElseThrow(() -> new NullPointerException("Username not found"));
        //String converId = chatSer.createNewConversation(userDetails, "Conv between " + userDetails.getFullName() + " and " + partner.getFullName());
        //chatSer.inviteUserToConversation(userDetails, partner, converId);


        String converId2 = chatSer.createNewConversation(userDetails, partner);
        
    }


}