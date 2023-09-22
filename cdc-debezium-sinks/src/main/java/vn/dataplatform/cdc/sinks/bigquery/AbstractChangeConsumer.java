package vn.dataplatform.cdc.sinks.bigquery;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.server.BaseChangeConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * @author tuan.nguyen3
 */
@Slf4j
public abstract class AbstractChangeConsumer extends BaseChangeConsumer implements DebeziumEngine.ChangeConsumer<ChangeEvent<Object, Object>>{

    public void initialize() {
        log.info("initialize");
    }
    public abstract void sendEvent(ChangeEvent<Object, Object> event);
    @Override
    public void handleBatch(List<ChangeEvent<Object, Object>> list, DebeziumEngine.RecordCommitter<ChangeEvent<Object, Object>> recordCommitter) throws InterruptedException {
        log.info("handleBatch value: {}", list);
        List<Integer> a = new Vector<>();
        Set<String> an = new HashSet<>();
    }
}
