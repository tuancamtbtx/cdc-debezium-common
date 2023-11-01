package vn.dataplatform.security.provider;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import vn.dataplatform.security.spi.TextCrypto;
import vn.dataplatform.security.spi.TextHash;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author tuan.nguyen3
 */
@AutoService(TextCrypto.class)
@Slf4j
public class CryptoProvider implements TextCrypto {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "mySuperSecretKey";

    @Override
    public String encrypt(String text) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String text) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public void initialize() throws Exception {

    }
}
