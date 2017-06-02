package se.tfmoney.microservice.oauth.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Created by Marcus Münger on 2017-05-23.
 */
public class JSONWebToken
{
    public static boolean isSigned(String jwt)
    {
        return Jwts.parser().isSigned(jwt);
    }

    public static String buildToken(String jwtPass, String id, String issuer, String subject, String audience,
                                    long ttlMillis)
    {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] secretByteArray = DatatypeConverter.parseBase64Binary(jwtPass);
        Key signingKey = new SecretKeySpec(secretByteArray, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                                 .setId(id)
                                 .setIssuedAt(now)
                                 .setSubject(subject)
                                 .setIssuer(issuer)
                                 .setAudience(audience)
                                 .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis >= 0)
        {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    public static Claims decryptToken(String jwtPass, String jwt) throws Exception
    {
        Claims claims = Jwts.parser()
                            .setSigningKey(DatatypeConverter.parseBase64Binary(jwtPass))
                            .parseClaimsJws(jwt)
                            .getBody();
        return claims;
    }

    public static String reencryptToken(String oldJWTPass, String newJWTPass, String jwt) throws Exception
    {
        Claims claims = decryptToken(oldJWTPass, jwt);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretByteArray = DatatypeConverter.parseBase64Binary(newJWTPass);
        Key signingKey = new SecretKeySpec(secretByteArray, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                                 .setId(claims.getId())
                                 .setIssuedAt(claims.getIssuedAt())
                                 .setSubject(claims.getSubject())
                                 .setIssuer(claims.getIssuer())
                                 .setAudience(claims.getAudience())
                                 .setExpiration(claims.getExpiration())
                                 .signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }
}
