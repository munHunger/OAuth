package se.tfmoney.microservice.oauth.util.jwt;

import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

/**
 * Created by Marcus Münger on 2017-05-23.
 */
public class JSONWebToken
{
    private static String secret = "so secret that no one will notice"; //TODO: read this from settings file

    public static void buildToken()
    {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretByteArray = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(secretByteArray, signatureAlgorithm.getJcaName());
    }
}
