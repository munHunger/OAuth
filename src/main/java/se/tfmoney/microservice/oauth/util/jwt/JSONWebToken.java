package se.tfmoney.microservice.oauth.util.jwt;

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
    private static String secret = "so secret that no one will notice"; //TODO: read this from settings file

    public static String buildToken(String id, String issuer, String subject, long ttlMillis)
    {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] secretByteArray = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(secretByteArray, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                                 .setId(id)
                                 .setIssuedAt(now)
                                 .setSubject(subject)
                                 .setIssuer(issuer)
                                 .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis >= 0)
        {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }
}
