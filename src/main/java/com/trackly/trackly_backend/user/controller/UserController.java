package com.trackly.trackly_backend.user.controller;

import com.trackly.trackly_backend.user.service.UserService;
import com.trackly.trackly_backend.user.dto.LoginRequest;
import com.trackly.trackly_backend.user.dto.LoginResponse;
import com.trackly.trackly_backend.user.dto.RegisterRequest;
import com.trackly.trackly_backend.user.dto.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
            ){
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
            ){
        LoginResponse loginResponse = userService.login(request);
        Cookie cookie = new Cookie("refreshToken",loginResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //set true in productions
        cookie.setPath("/");
        cookie.setMaxAge(7*24*60*60); // 7 days
        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponse(loginResponse.getToken(),null));
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile(){
        return  ResponseEntity.ok("You're authenticated");

    }
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Welcome Admin");
    }

}
