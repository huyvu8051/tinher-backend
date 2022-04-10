package com.bobvu.tinherbackend.cassandra.mapper;

import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.match.ProfileResponse;
import com.bobvu.tinherbackend.setting.UserSettingDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

   ProfileResponse toProfileResponse(com.bobvu.tinherbackend.elasticsearch.User entity);


    //@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void setting(UserSettingDTO request, @MappingTarget User user);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void setting(UserSettingDTO request, @MappingTarget com.bobvu.tinherbackend.elasticsearch.User user);

    com.bobvu.tinherbackend.elasticsearch.User toEsEntity(User user);

    //Admin toEntity(HostAddRequest dto);
}