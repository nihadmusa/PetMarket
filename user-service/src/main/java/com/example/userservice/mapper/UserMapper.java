package com.example.userservice.mapper;

import com.example.userservice.dao.entity.UserEntity;
import com.example.userservice.dto.response.UserContactResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
     UserContactResponseDto entityToDto(UserEntity entity);
}
