package com.x.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成 JWT
     * 使用 HS256 算法，密钥由传入的 secretKey 字符串生成
     *
     * @param secretKey 必须是至少 32 字节长度的字符串（256 位）
     * @param ttlMillis JWT 过期时间(毫秒)
     * @param claims    设置的信息
     * @return JWT 字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 确保密钥长度安全
        validateSecretKeyLength(secretKey);

        // 生成 JWT 的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 构建签名密钥
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 使用非弃用 API 构建 JWT
        return Jwts.builder()
                .setClaims(claims) // 设置自定义声明（必须先设置）
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(exp)
                .compact();
    }

    /**
     * Token 解密
     *
     * @param secretKey 生成 JWT 时使用的相同密钥
     * @param token     JWT 字符串
     * @return 解析后的 Claims
     * @throws IllegalArgumentException 如果 token 无效或已过期
     */
    public static Claims parseJWT(String secretKey, String token) {
        try {
            // 构建签名密钥
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // 使用非弃用 API 解析 JWT
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return jws.getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // 捕获 JWT 相关异常并统一处理
            throw new IllegalArgumentException("无效的 JWT token: " + e.getMessage(), e);
        }
    }

    /**
     * 验证密钥长度是否安全（至少 32 字节）
     */
    private static void validateSecretKeyLength(String secretKey) {
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("HS256 算法的密钥长度必须至少 32 字节");
        }
    }
}
