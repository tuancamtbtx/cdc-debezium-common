package vn.dataplatform.cdc.sinks.bigquery.batchsizewait;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

/**
 * @author tuan.nguyen3
 */
@Dependent
@Named("NoBatchSizeWait")
public class NoBatchSizeWait implements InterfaceBatchSizeWait{
  public void waitMs(long numRecordsProcessed, Integer processingTimeMs) {
  }

}
