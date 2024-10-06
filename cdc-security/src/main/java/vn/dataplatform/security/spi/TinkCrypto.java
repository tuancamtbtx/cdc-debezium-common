package vn.dataplatform.security.spi;

public interface TinkCrypto {
    public String encrypt(String text);
    public String decrypt(String encrypted);
}
