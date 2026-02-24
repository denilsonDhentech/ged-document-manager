package com.dhensouza.ged.infrastructure.configuration.security;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class KeyUtils {
    private final KeyPair keyPair;

    public KeyUtils() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keyPair = generator.generateKeyPair();
    }

    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }
}
