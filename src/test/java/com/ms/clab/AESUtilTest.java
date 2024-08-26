package com.ms.clab;

import com.ms.clab.util.AESUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AESUtilTest {

    @Autowired
    private AESUtil aesUtil;

    private String originalText;
    private String encryptedText;

    @BeforeEach
    public void setUp() {
        originalText = "sylee";
    }

    @Test
    public void testEncryption() throws Exception {
        encryptedText = aesUtil.encrypt(originalText);
        System.out.println("Encrypted Text: " + encryptedText);
        // 암호화된 텍스트가 원본 텍스트와 다름을 확인
        assertEquals(false, originalText.equals(encryptedText));
    }

    @Test
    public void testDecryption() throws Exception {
        encryptedText = aesUtil.encrypt(originalText);
        String decryptedText = aesUtil.decrypt(encryptedText);
        System.out.println("Decrypted Text: " + decryptedText);
        // 복호화된 텍스트가 원본 텍스트와 동일함을 확인
        assertEquals(originalText, decryptedText);
    }

    @Test
    public void testInvalidDecryption() {
        // 잘못된 암호화 텍스트로 복호화 시도
        assertThrows(Exception.class, () -> {
            aesUtil.decrypt("InvalidEncryptedText");
        });
    }
}
