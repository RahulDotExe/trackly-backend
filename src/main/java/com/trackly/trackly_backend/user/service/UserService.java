package com.trackly.trackly_backend.user.service;

import com.trackly.trackly_backend.config.JwtUtil;
import com.trackly.trackly_backend.user.exceptions.EmailAlreadyExistsException;
import com.trackly.trackly_backend.user.exceptions.InvalidCredentialsException;
import com.trackly.trackly_backend.user.enums.Role;
import com.trackly.trackly_backend.user.repository.UserRepository;
import com.trackly.trackly_backend.user.dto.LoginRequest;
import com.trackly.trackly_backend.user.dto.LoginResponse;
import com.trackly.trackly_backend.user.dto.RegisterRequest;
import com.trackly.trackly_backend.user.dto.UserResponse;
import com.trackly.trackly_backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private  final RefreshTokenService refreshTokenService;


    public UserResponse register(RegisterRequest request){

        String normalizedEmail = request.getEmail().toLowerCase();
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(normalizedEmail)
                .password(hashedPassword)
                .role(Role.USER)
                .build();

        try{
            User savedUser = userRepository.save(user);

            return new UserResponse(
                    savedUser.getId(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getCreatedAt()
            );
        } catch (DataIntegrityViolationException e){
            throw new EmailAlreadyExistsException("Email already exists");
        }
    }

    public LoginResponse login(LoginRequest request){

        String normalizedEmail = request.getEmail().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->  new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");

        }

        String token = jwtUtil.generateToken(user.getId(),user.getRole().name());
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return new LoginResponse(token, refreshToken);
    }

    public LoginResponse refresh(String rawRefreshToken){
        return refreshTokenService.refresh(rawRefreshToken);
    }




}
