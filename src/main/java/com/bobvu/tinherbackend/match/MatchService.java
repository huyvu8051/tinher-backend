package com.bobvu.tinherbackend.match;

import com.bobvu.tinherbackend.cassandra.model.User;

public interface MatchService {
    PageResponse<ProfileResponse> findAllSuitablePerson(User user, FindSuitablePersonRequest request);

    void likePartner(User userDetails, LikePartnerRequest request);
}