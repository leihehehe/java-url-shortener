package com.leih.url.common.util;

import com.leih.url.common.exception.BizException;
import com.leih.url.common.model.LoggedInUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
@Slf4j
public class JWTUtil {
    private static final String SUBJECT = "easy-url-shortener";
    private static final String SECRET = "YOURTOKENHEREHAHAHA";
    private static final String TOKEN_PREFIX = "easy-url-shortener";
    // EXPIRATION TIME : 7 hours
    private static final long EXPIRED = 1000*60*60*24*7;
    /**
     * Generate JWT token
     * @param loggedInUser
     * @return
     */
    public static String generateJsonWebToken(LoggedInUser loggedInUser){
        if(loggedInUser==null){
            throw new NullPointerException("Logged-in user is null");
        }
        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("avatar", loggedInUser.getAvatar())
                .claim("username", loggedInUser.getUsername())
//                .claim("phone", loggedInUser.getPhone())
                .claim("auth", loggedInUser.getAuth())
//                .claim("email", loggedInUser.getEmail())
                .claim("account_no",loggedInUser.getAccountNo())
                .setIssuedAt(new Date())
                .setExpiration(new Date(CommonUtil.getCurrentTimestamp() + EXPIRED))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        token = TOKEN_PREFIX+token;
        return token;
    }

    public static Claims checkJWT(String token){
        try{
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
            return claims;
        }catch (Exception e){
            log.error("JWT decryption failed");
            return null;
        }
    }
}
