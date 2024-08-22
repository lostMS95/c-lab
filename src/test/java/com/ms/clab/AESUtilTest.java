package com.ms.clab;

import com.ms.clab.util.AESUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AESUtilTest {
    private String originalText;
    private String encryptedText;

    @BeforeEach
    public void setUp() {
        originalText = "yjkim";
    }

    @Test
    public void testEncryption() throws Exception {
        encryptedText = AESUtil.encrypt(originalText);
        System.out.println("Encrypted Text: " + encryptedText);
        // 암호화된 텍스트가 원본 텍스트와 다름을 확인
        assertEquals(false, originalText.equals(encryptedText));
    }

    @Test
    public void testDecryption() throws Exception {
        encryptedText = AESUtil.encrypt(originalText);
        String decryptedText = AESUtil.decrypt(encryptedText);
        System.out.println("Decrypted Text: " + decryptedText);
        // 복호화된 텍스트가 원본 텍스트와 동일함을 확인
        assertEquals(originalText, decryptedText);
    }

    @Test
    public void testInvalidDecryption() {
        // 잘못된 암호화 텍스트로 복호화 시도
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt("InvalidEncryptedText");
        });
    }
}
