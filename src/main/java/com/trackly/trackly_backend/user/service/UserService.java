package com.trackly.trackly_backend.user.service;

import com.trackly.trackly_backend.config.JwtUtil;
import com.trackly.trackly_backend.user.EmailAlreadyExistsException;
import com.trackly.trackly_backend.user.InvalidCredentialsException;
import com.trackly.trackly_backend.user.Role;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public UserResponse register(RegisterRequest request){

        String normalizedEmail = request.getEmail().toLowerCase();
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(normalizedEmail)
                .password(hashedPassword)
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
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
        return new LoginResponse(token);
    }



}
