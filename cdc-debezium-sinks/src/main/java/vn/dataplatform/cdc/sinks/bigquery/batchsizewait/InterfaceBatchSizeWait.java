package vn.dataplatform.cdc.sinks.bigquery.batchsizewait;

/**
 * @author tuan.nguyen3
 */
public interface InterfaceBatchSizeWait {
  default void initizalize() {
  }

  void waitMs(long numRecordsProcessed, Integer processingTimeMs) throws InterruptedException;
}
