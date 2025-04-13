package com.autoMotiveMes.utils;

import com.autoMotiveMes.common.exception.AuthException;
import com.autoMotiveMes.common.exception.GlobalException;
import com.autoMotiveMes.entity.system.SysUser;
import com.autoMotiveMes.mapper.system.SysUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    @Value("${security.jwt.secret}") // 在 application.yml 中配置
    private String secret;

    @Value("${security.jwt.expiration}") // 过期时间（秒）
    private int expiration;

    private final SysUserMapper userMapper;

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
        // 解析令牌
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 获取令牌的过期时间
        Date expiration = claims.getExpiration();

        // 检查过期时间是否晚于当前时间
        if (expiration != null && expiration.before(new Date())) {
            throw new AuthException("登录信息已过期，请重新登录");
        }

        String username = claims.getSubject();
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new GlobalException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new AuthException("抱歉，您的账号已停用");
        }
        if (user.getAccountLocked() == 0) {
            throw new AuthException("抱歉，您的账号已锁定");
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