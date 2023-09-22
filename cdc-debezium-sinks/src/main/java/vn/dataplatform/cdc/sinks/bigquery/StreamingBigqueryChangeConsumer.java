package vn.dataplatform.cdc.sinks.bigquery;

import io.debezium.engine.ChangeEvent;
import io.debezium.server.BaseChangeConsumer;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * @author tuan.nguyen3
 */
@Named("stream_bigquery")
@Dependent
public class StreamingBigqueryChangeConsumer extends AbstractChangeConsumer {
    @Override
    public void sendEvent(ChangeEvent<Object, Object> event) {

    }
}
