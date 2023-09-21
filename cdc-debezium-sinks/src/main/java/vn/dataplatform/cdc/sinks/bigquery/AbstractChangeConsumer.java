package vn.dataplatform.cdc.sinks.bigquery;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.server.BaseChangeConsumer;

import java.util.List;

/**
 * @author tuan.nguyen3
 */
public class AbstractChangeConsumer extends BaseChangeConsumer implements DebeziumEngine.ChangeConsumer<ChangeEvent<Object, Object>>{
    @Override
    public void handleBatch(List<ChangeEvent<Object, Object>> list, DebeziumEngine.RecordCommitter<ChangeEvent<Object, Object>> recordCommitter) throws InterruptedException {

    }
}
