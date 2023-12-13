package vn.dataplatform.security.loader;

import lombok.NonNull;
import vn.dataplatform.security.utils.ServiceLoaderUtils;

import java.util.List;

/**
 * @author tuan.nguyen3
 */
public class GenericServiceLoader<T> {
    Class<T> clazz;

    public GenericServiceLoader(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Gets the first available implementation of the service interface using the Java Service Provider Interface (SPI) mechanism.
     *
     * @return the first available implementation of the service interface
     */
    public T getInstance() {
        if (clazz == null) {
            throw new IllegalArgumentException("class cannot be null");
        }
        List<T> providersList = ServiceLoaderUtils.findServiceProviders(clazz);
        return providersList.stream().findFirst().orElseThrow(RuntimeException::new);
    }

    /**
     * Gets the first available implementation of the service interface using the Java Service Provider Interface (SPI) mechanism.
     *
     * @return the first available implementation of the service interface
     */
    public T getInstance(@NonNull String name) {
        List<T> providersList = ServiceLoaderUtils.findServiceProviders(clazz);
        return providersList.stream().filter(provider -> name.equals(provider.getClass().getName())).findFirst().orElseThrow(RuntimeException::new);
    }

    /**
     * Gets the first available implementation of the service interface using the Java Service Provider Interface (SPI) mechanism.
     *
     * @return the first available implementation of the service interface
     */
    public T getInstance(@NonNull Class<T> clazz) {
        List<T> providersList = ServiceLoaderUtils.findServiceProviders(clazz);
        return providersList.stream().filter(provider -> clazz.equals(provider.getClass())).findFirst().orElseThrow(RuntimeException::new);
    }
}
