package com.bobvu.tinherbackend.match;

import com.bobvu.tinherbackend.cassandra.model.User;

public interface MatchService {
    PageResponse<User> findAllSuitablePerson(User user, FindSuitablePersonRequest request);

    void likePartner(User userDetails, LikePartnerRequest request);

    void boost(User userDetails);
}