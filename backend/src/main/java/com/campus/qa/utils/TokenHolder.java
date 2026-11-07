package com.campus.qa.utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录令牌存储（内存态）
 * 登录后由服务端生成 token 并返回前端，前端后续请求携带该 token 完成身份识别。
 * 说明：课设项目使用内存存储，服务重启后需重新登录，不引入第三方依赖。
 */
public class TokenHolder {

    /** token -> 用户id */
    private static final Map<String, Long> TOKEN_MAP = new ConcurrentHashMap<String, Long>();

    /** 生成 token 并绑定用户id */
    public static String createToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        TOKEN_MAP.put(token, userId);
        return token;
    }

    /** 根据 token 获取用户id，token 无效时返回 null */
    public static Long getUserId(String token) {
        if (token == null || token.trim().length() == 0) {
            return null;
        }
        return TOKEN_MAP.get(token);
    }

    /** 移除 token（退出登录） */
    public static void remove(String token) {
        if (token != null) {
            TOKEN_MAP.remove(token);
        }
    }
}
