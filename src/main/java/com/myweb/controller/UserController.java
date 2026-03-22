package com.myweb.controller;

import com.myweb.dto.LoginRequest;
import com.myweb.dto.RegisterRequest;
import com.myweb.dto.Result;
import com.myweb.security.JwtTokenProvider;
import com.myweb.security.JwtUserPrincipal;
import com.myweb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口：登录、注册
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(principal.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", principal.getUsername());
        data.put("nickname", principal.getUser().getNickname());
        data.put("email", principal.getUser().getEmail());
        data.put("avatar", principal.getUser().getAvatar());
        data.put("role", principal.getUser().getRole());
        return Result.ok(data);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword(),
                request.getNickname(), request.getEmail());
        return Result.ok();
    }
}
