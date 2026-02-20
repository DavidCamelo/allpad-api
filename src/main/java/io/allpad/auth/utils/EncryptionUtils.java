package io.allpad.auth.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class EncryptionUtils {
    private static final String ALGORITHM = "AES";

    public static String createSecretKey() {
        try {
            var keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            var secureRandom = new SecureRandom();
            keyGenerator.init(128, secureRandom);
            var secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("Error creating the encryption key {}", e.getMessage(), e);
        }
        return null;
    }

    public static String decryptContent(String secretKey, String encryptedContent) {
        try {
            var encryptedBytes = Base64.getDecoder().decode(encryptedContent);
            var salt = new byte[8];
            System.arraycopy(encryptedBytes, 8, salt, 0, 8);
            var content = new byte[encryptedBytes.length - 16];
            System.arraycopy(encryptedBytes, 16, content, 0, content.length);
            var pass = secretKey.getBytes(StandardCharsets.UTF_8);
            var md5 = MessageDigest.getInstance("MD5");
            var dx = new byte[0];
            var saltedBytes = new byte[48];
            var saltedBytesIndex = 0;
            while (saltedBytesIndex < 48) {
                md5.update(dx);
                md5.update(pass);
                md5.update(salt);
                dx = md5.digest();
                var remaining = 48 - saltedBytesIndex;
                var length = Math.min(dx.length, remaining);
                System.arraycopy(dx, 0, saltedBytes, saltedBytesIndex, length);
                saltedBytesIndex += length;
            }
            var key = new byte[32];
            var iv = new byte[16];
            System.arraycopy(saltedBytes, 0, key, 0, 32);
            System.arraycopy(saltedBytes, 32, iv, 0, 16);
            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            var keySpec = new SecretKeySpec(key, ALGORITHM);
            var ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            var decryptedBytes = cipher.doFinal(content);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting content: {}", e.getMessage());
        }
        return null;
    }
}
