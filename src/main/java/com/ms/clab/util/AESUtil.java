package com.ms.clab.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESUtil {

    private final String algorithm;
    private final byte[] keyValue;

    public AESUtil(@Value("${encryption.algorithm}") String algorithm,
                   @Value("${encryption.secret-key}") String secretKey) {
        this.algorithm = algorithm;
        this.keyValue = secretKey.getBytes();
    }

    public String encrypt(String valueToEnc) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyValue, algorithm);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedVal = cipher.doFinal(valueToEnc.getBytes());
        return Base64.getEncoder().encodeToString(encryptedVal);
    }

    public String decrypt(String encryptedValue) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(keyValue, algorithm);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedVal = cipher.doFinal(decodedValue);
        return new String(decryptedVal);
    }
}