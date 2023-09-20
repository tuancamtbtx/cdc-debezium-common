package vn.dataplatform.security.provider;

import vn.dataplatform.security.spi.TextCrypto;

/**
 * @author tuan.nguyen3
 */
public class CryptoProvider implements TextCrypto {
    @Override
    public String encrypt(String text) throws Exception {
        return null;
    }

    @Override
    public String decrypt(String text) throws Exception {
        return null;
    }

    @Override
    public void initialize() throws Exception {

    }
}
