package vn.dataplatform.core.serviceloader;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * @author tuan.nguyen3
 */
public class ServiceLoaderUtils {
    public ServiceLoaderUtils() {}

    /**
     * Finds and returns a list of service providers of the given class type.
     *
     * @param <T> the type of the service provider being searched for
     * @param clazz the class type of the service provider being searched for
     * @return a list of all service providers of the given class type
     * @throws ServiceConfigurationError if there is an error while accessing the service providers
     */
    public static <T> List<T> findServiceProviders(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = getServiceLoader(clazz);
        List<T> providerList = new ArrayList<>();
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            safelyInstantiate(providerList, iterator);
        }
        return providerList;
    }

    /**
     * Returns a {@link ServiceLoader} instance for the given service provider class.
     *
     * @param <T> the type of the service provider being searched for
     * @param clazz the class type of the service provider being searched for
     * @return a {@link ServiceLoader} instance for the given service provider class
     */
    private static <T> ServiceLoader<T> getServiceLoader(Class<T> clazz) {
        ServiceLoader<T> loader;
        SecurityManager security = System.getSecurityManager();
        if (security == null) {
            loader = ServiceLoader.load(clazz);
        } else {
            final PrivilegedAction<ServiceLoader<T>> action = () -> ServiceLoader.load(clazz);
            loader = AccessController.doPrivileged(action);
        }
        return loader;
    }

    /**
     * Safely instantiates a service provider and adds it to the list of providers.
     *
     * @param <T> the type of the service provider being added to the list
     * @param providerList the list of service providers to add to
     * @param iterator the iterator to use to retrieve the next service provider
     */
    private static <T> void safelyInstantiate(List<T> providerList, Iterator<T> iterator) {
        try {
            T provider = iterator.next();
            providerList.add(provider);
        } catch (ServiceConfigurationError e) {
            e.printStackTrace();
        }
    }
}
