package vn.dataplatform.cdc.schema.apicurio;

import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;

/**
 * @author tuan.nguyen3
 */
public class APICurioRegistry {
    public static void main(String[] args) {
        String registryUrl = "http://localhost:8080/api";
        RegistryClient client = RegistryClientFactory.create(registryUrl);

    }
}
