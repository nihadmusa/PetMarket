package com.example.userservice.service;

import com.example.userservice.dao.entity.UserEntity;
import com.example.userservice.dao.repository.UserRepository;
import com.example.userservice.dto.request.RenewRequestdto;
import com.example.userservice.dto.request.SignInRequestDto;
import com.example.userservice.dto.request.SignUpRequestDto;
import com.example.userservice.dto.response.SignInResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final UserRepository repository;

    public SignInResponseDto signIn(SignInRequestDto dto){
        var user = repository.findByPhoneNumber(dto.phoneNumber()).orElseThrow(
                () -> new RuntimeException("telefon nomresi tapilmadi")
        );
        if (!encoder.matches(dto.password(), user.getPassword())){
            throw new RuntimeException("sifre yanlisdir");
        }
        return new SignInResponseDto(
                jwtService.generateAccessToken(user.getId()),
                jwtService.generateRefreshToken(user.getId())
        );

    }

    public void signUp(SignUpRequestDto dto){
        var isRegistered = repository.findByPhoneNumber(dto.phoneNumber());
        if (isRegistered.isPresent()){
            throw new RuntimeException("bu nomre artiq qeydiyyatdadir");
        }
        var entity = UserEntity.builder()
                .fullName(dto.fullName())
                .phoneNumber(dto.phoneNumber())
                .password(encoder.encode(dto.password()))
                .build();

        repository.save(entity);
    }

    public SignInResponseDto renew(RenewRequestdto dto){
        var userId = jwtService.getUser(dto.refreshToken());
        return new SignInResponseDto(
                jwtService.generateAccessToken(userId),
                jwtService.generateRefreshToken(userId)
        );
    }

}
