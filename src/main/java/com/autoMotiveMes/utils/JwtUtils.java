package com.autoMotiveMes.utils;

import com.autoMotiveMes.common.exception.BusinessException;
import com.autoMotiveMes.common.response.ErrorCode;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.mapper.system.SysUserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 实现功能【JWT 工具类】
 *
 * @author li.hongyu
 * @date 2025-02-15 10:13:17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private int expiration;

    private final SysUserMapper userMapper;
    private final RedisTemplate<String, SysUser> userRedisTemplate;

    // 生成密钥
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成令牌
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证令牌
    public void validateToken(String token) {
        try {
            // 解析令牌（自动验证签名和过期时间）
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 如果解析成功，继续验证用户状态
            String username = claims.getSubject();

            // 从 Redis 中获取用户信息
            SysUser user = userRedisTemplate.opsForValue().get("user:" + username);

            // 缓存未命中时查询数据库
            if (user == null) {
                user = userMapper.selectByUsername(username);
                if (user == null) {
                    throw new BadCredentialsException(ErrorCode.USER_NOT_EXISTS.getMsg());
                }
                // 将查询结果写入缓存
                userRedisTemplate.opsForValue().set(
                        "user:" + username,
                        user,
                        expiration,
                        TimeUnit.SECONDS
                );
            }

            if (user.getStatus() == 0) {
                throw new AccessDeniedException(ErrorCode.ACCOUNT_DISABLED.getMsg());
            }
            if (user.getAccountLocked() == 0) {
                throw new AccessDeniedException(ErrorCode.ACCOUNT_LOCKED.getMsg());
            }
        } catch (ExpiredJwtException ex) {
            throw new BadCredentialsException(ErrorCode.LOGIN_INFO_EXPIRED.getMsg());
        } catch (JwtException ex) {
            throw new BadCredentialsException(ErrorCode.ERROR_TOKEN.getMsg());
        }
    }

    // 从令牌中获取用户名
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}