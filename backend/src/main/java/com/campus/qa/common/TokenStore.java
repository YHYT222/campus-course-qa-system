package com.campus.qa.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版Token存储（课设简化实现，重启后失效需重新登录）
 */
@Component
public class TokenStore {

    private final Map<String, Long> store = new ConcurrentHashMap<>();

    public String create(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        store.put(token, userId);
        return token;
    }

    public Long get(String token) {
        return token == null ? null : store.get(token);
    }

    public void remove(String token) {
        if (token != null) {
            store.remove(token);
        }
    }
}
