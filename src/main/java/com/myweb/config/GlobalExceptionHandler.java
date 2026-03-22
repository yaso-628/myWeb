package com.myweb.config;

import com.myweb.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：认证失败返回 401
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<?>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(401, "用户名或密码错误"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Result<?>> handleUsernameNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(401, "用户不存在"));
    }
}
