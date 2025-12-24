package com.accounting.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类 (JSON Web Token Utility)
 * <p>
 * 负责 Token 的生成 (Sign) 和 解析 (Verify)。
 * 使用 HMAC SHA256 对称加密算法。
 * </p>
 */
public class JwtUtil {
    private final Key key;
    private final long ttlMillis;
    
    /**
     * @param secret 密钥 (需保密，不能硬编码在代码中，建议从环境变量读取)
     * @param ttlMillis 默认有效期（毫秒）
     */
    public JwtUtil(String secret, long ttlMillis) {
        // 使用 HMAC-SHA 算法生成密钥实例
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMillis = ttlMillis;
    }
    
    /**
     * 生成 Token
     * <p>
     * Payload 中包含标准声明：
     * - sub (Subject): 主体标识（通常是 User ID）
     * - iat (Issued At): 签发时间
     * - exp (Expiration): 过期时间
     * </p>
     */
    public String generate(String subject, long customTtlMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + customTtlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generate(String subject) {
        return generate(subject, this.ttlMillis);
    }
    
    /**
     * 解析 Token 获取 Subject (User ID)
     * <p>
     * 如果 Token 被篡改（签名不匹配）或已过期，此处会抛出异常 (JwtException)。
     * 调用方需捕获异常以处理认证失败的情况。
     * </p>
     */
    public String parseSubject(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
