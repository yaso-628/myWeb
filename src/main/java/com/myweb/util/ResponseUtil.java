package com.myweb.util;

import com.myweb.dto.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 响应工具（如未走 Controller 时的 JSON 输出）
 */
public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static void writeJson(HttpServletResponse response, int status, Result<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = result.getData() == null && result.getMessage() != null
                ? String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}", result.getCode(), result.getMessage())
                : "{\"code\":200,\"message\":\"success\",\"data\":null}";
        response.getWriter().write(json);
    }
}
