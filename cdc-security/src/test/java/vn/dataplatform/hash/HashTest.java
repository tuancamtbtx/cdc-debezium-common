package vn.dataplatform.hash;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import vn.dataplatform.security.spi.TextHash;

/**
 * @author tuan.nguyen3
 */
@Slf4j
public class HashTest {
    @Test
    public void testHashString() throws Exception {
        String text = "Hello World";
        String hashed = TextHash.getInstance("vn.dataplatform.security.provider.TextHashProvider").hash(text);
        log.info("raw: {} - hash: {}", text, hashed);
    }
}
