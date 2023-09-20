package vn.dataplatform.security.provider;

import vn.dataplatform.security.spi.TextHash;

/**
 * @author tuan.nguyen3
 */
public class TextHashProvider implements TextHash {
    @Override
    public String hash(String text) throws Exception {
        return null;
    }

    @Override
    public void initialize() throws Exception {

    }
}
