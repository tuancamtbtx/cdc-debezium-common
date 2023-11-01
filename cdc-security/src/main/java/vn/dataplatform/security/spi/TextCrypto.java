package vn.dataplatform.security.spi;

import vn.dataplatform.security.loader.GenericServiceLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

/**
 * @author tuan.nguyen3
 */
public interface TextCrypto {
    Map<String, TextCrypto> textCryptoMap = new HashMap<>();
    static TextCrypto getInstance(String clazzProviderName) {
        try {
            TextCrypto phoneNumberFormatter = textCryptoMap.get(clazzProviderName);
            if (phoneNumberFormatter != null) {
                return phoneNumberFormatter;
            } else {
                GenericServiceLoader<TextCrypto> loader = new GenericServiceLoader<>(TextCrypto.class);
                TextCrypto newInstance = loader.getInstance(clazzProviderName);
                TextCrypto oldInstance = textCryptoMap.putIfAbsent(clazzProviderName, newInstance);
                return oldInstance == null ? newInstance : oldInstance;
            }
        } catch (ServiceConfigurationError e) {
            throw new RuntimeException(e);
        }
    }
    public String encrypt(String text) throws Exception;
    public String decrypt(String text) throws Exception;
    void initialize() throws Exception;
}
