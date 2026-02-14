package com.trackly.trackly_backend.user;

import com.trackly.trackly_backend.user.dto.LoginRequest;
import com.trackly.trackly_backend.user.dto.LoginResponse;
import com.trackly.trackly_backend.user.dto.RegisterRequest;
import com.trackly.trackly_backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.TargetEmbeddable;
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
            @Valid @RequestBody LoginRequest request
            ){
        return ResponseEntity.ok(userService.login(request));
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
