package vn.dataplatform.security.provider;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import vn.dataplatform.security.spi.TextHash;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author tuan.nguyen3
 */
@Slf4j
@AutoService(TextHash.class)
public class TextHashProvider implements TextHash {
    @Override
    public String hash(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void initialize() throws Exception {

    }
}
