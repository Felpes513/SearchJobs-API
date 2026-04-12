package com.searchjobs.api.infrastructure.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptionService {

    private final TextEncryptor encryptor;

    public EncryptionService(
            @Value("${encryption.secret}") String secret,
            @Value("${encryption.salt}") String salt
    ) {
        this.encryptor = Encryptors.text(secret, salt);
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) return null;
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isBlank()) return null;
        return encryptor.decrypt(cipherText);
    }

    /**
     * Returns first 3 chars + **** + last 4 chars.
     * E.g. "sk-abc...xyz1234" → "sk-****1234"
     */
    public String mask(String plainText) {
        if (plainText == null || plainText.isBlank()) return null;
        if (plainText.length() <= 7) return "****";
        return plainText.substring(0, Math.min(3, plainText.length()))
                + "****"
                + plainText.substring(plainText.length() - 4);
    }
}
