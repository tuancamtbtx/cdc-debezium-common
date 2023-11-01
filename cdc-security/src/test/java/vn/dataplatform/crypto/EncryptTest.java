package vn.dataplatform.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import vn.dataplatform.security.spi.TextCrypto;
import vn.dataplatform.security.spi.TextHash;

/**
 * @author tuan.nguyen3
 */
@Slf4j
public class EncryptTest {
    @Test
    public void testEncryptString() throws Exception {
        String text = "Hello World";
        String hashed = TextCrypto.getInstance("vn.dataplatform.security.provider.CryptoProvider").encrypt(text);
        log.info("raw: {} - encrypt: {}", text, hashed);
    }
}
