package vn.dataplatform.security.spi;

/**
 * @author tuan.nguyen3
 */
public interface TextCrypto {
    public String encrypt(String text) throws Exception;
    public String decrypt(String text) throws Exception;
    void initialize() throws Exception;
}
