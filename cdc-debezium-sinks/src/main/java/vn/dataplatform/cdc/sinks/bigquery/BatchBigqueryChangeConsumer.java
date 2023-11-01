package vn.dataplatform.cdc.sinks.bigquery;

import io.debezium.engine.ChangeEvent;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * @author tuan.nguyen3
 */
@Named("batch_bigquery")
@Dependent
public class BatchBigqueryChangeConsumer extends AbstractChangeConsumer {
    /**
     * @param event
     */
    @Override
    public void sendEvent(ChangeEvent<Object, Object> event) {

    }

    /**
     * @return
     */
    @Override
    public boolean supportsTombstoneEvents() {
        return super.supportsTombstoneEvents();
    }
}
