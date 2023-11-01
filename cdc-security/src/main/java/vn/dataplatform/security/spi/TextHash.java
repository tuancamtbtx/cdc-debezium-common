package vn.dataplatform.security.spi;

import vn.dataplatform.security.loader.GenericServiceLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

/**
 * @author tuan.nguyen3
 */
public interface TextHash {
    Map<String, TextHash> textHashMap = new HashMap<>();
    static TextHash getInstance(String clazzProviderName) {
        try {
            TextHash phoneNumberFormatter = textHashMap.get(clazzProviderName);
            if (phoneNumberFormatter != null) {
                return phoneNumberFormatter;
            } else {
                GenericServiceLoader<TextHash> loader = new GenericServiceLoader<>(TextHash.class);
                TextHash newInstance = loader.getInstance(clazzProviderName);
                TextHash oldInstance = textHashMap.putIfAbsent(clazzProviderName, newInstance);
                return oldInstance == null ? newInstance : oldInstance;
            }
        } catch (ServiceConfigurationError e) {
            throw new RuntimeException(e);
        }
    }
    public String hash(String text) throws Exception;
    void initialize() throws Exception;
}
