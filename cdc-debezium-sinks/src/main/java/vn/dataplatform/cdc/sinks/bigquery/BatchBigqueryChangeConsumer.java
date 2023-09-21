package vn.dataplatform.cdc.sinks.bigquery;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * @author tuan.nguyen3
 */
@Named("batch_bigquery")
@Dependent
public class BatchBigqueryChangeConsumer extends AbstractChangeConsumer {
}
