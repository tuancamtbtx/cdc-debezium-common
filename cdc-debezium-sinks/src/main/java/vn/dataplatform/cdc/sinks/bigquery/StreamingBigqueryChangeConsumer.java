package vn.dataplatform.cdc.sinks.bigquery;

import io.debezium.server.BaseChangeConsumer;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * @author tuan.nguyen3
 */
@Named("stream_bigquery")
@Dependent
public class StreamingBigqueryChangeConsumer extends AbstractChangeConsumer {
}
