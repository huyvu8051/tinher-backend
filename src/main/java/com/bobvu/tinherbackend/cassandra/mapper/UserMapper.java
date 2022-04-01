package com.bobvu.tinherbackend.cassandra.mapper;

import com.bobvu.tinherbackend.cassandra.model.User;
import com.bobvu.tinherbackend.setting.UserSettingDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // @Mapping(source = "nonLocked", target = "isNonLocked")
   // HostResponse toDto(Admin entity);


    //@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void setting(UserSettingDTO request, @MappingTarget User user);

    //Admin toEntity(HostAddRequest dto);
}