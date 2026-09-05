package com.example.userservice.service;

import com.example.userservice.dao.repository.UserRepository;
import com.example.userservice.dto.request.UserUpdateDto;
import com.example.userservice.dto.response.UserContactResponseDto;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserContactResponseDto getUserContact(UUID id){
        var entity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("istifadeci tapilmadi"));
        return mapper.entityToDto(entity);
    }

    public UserContactResponseDto updateUser(UUID userId, UserUpdateDto dto){
        var entity = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("istifadeci tapilmadi"));
        if (dto.getFullName() != null) entity.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        return mapper.entityToDto(repository.save(entity));
    }
}
