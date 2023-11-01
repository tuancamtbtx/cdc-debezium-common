package vn.dataplatform.cdc.server;

import io.debezium.server.DebeziumServer;
import jakarta.inject.Inject;

/**
 * @author tuan.nguyen3
 */
public class MainApp {
    @Inject
    DebeziumServer debeziumServer;
}
