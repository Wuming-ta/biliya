package com.jfeat.ext.plugin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * 适配 springboot 系统的accessToken
 * @author jackyhuang
 * @date 2018/6/9
 */
public class JWTService {

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private long ttlMillis = 72 * 3600 * 1000;
    private String tokenType = "Bearer";
    private String encodedKey;

    public String getEncodedKey() {
        return encodedKey;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }

    public long getTtlMillis() {
        return ttlMillis;
    }

    public void setTtlMillis(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void init(String encodedKey) {
        init(encodedKey, this.ttlMillis);
    }

    public void init(String encodedKey, Long ttlMillis) {
        setEncodedKey(encodedKey);
        setTtlMillis(ttlMillis);
    }

    public String createToken(Long tenantId, Long userId, String account) {
        return createJWT(tenantId, userId, account, getTtlMillis());
    }

    public Claims parseToken(String token) {
        try {
            Claims claims = parseJWT(token);
            return claims;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getExpiresIn() {
        return getTtlMillis();
    }

    public String getTokenType() {
        return getTokenType();
    }

    private String createJWT(Long tenantId, Long userId, String account, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("tenantId", tenantId + "")
                .claim("userId", userId + "")
                .claim("account", account)
                .setIssuedAt(now)
                .setId(userId + "")
                .setSubject(account)
                .signWith(getSignatureAlgorithm(), getSecretKey());
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    private Claims parseJWT(String jwt) throws Exception{
        Claims claims = Jwts.parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    private SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    private Key deserializeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        Key key = new SecretKeySpec(decodedKey, getSignatureAlgorithm().getJcaName());
        return key;
    }

    private Key getSecretKey() {
        return deserializeKey(getEncodedKey());
    }

    private String serializeKey(Key key) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        return encodedKey;
    }

}
