package vn.dataplatform.cdc.sinks.bigquery.batchsizewait;

import io.debezium.config.CommonConnectorConfig;
import io.debezium.server.DebeziumMetrics;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tuan.nguyen3
 */
@Dependent
@Named("MaxBatchSizeWait")
public class MaxBatchSizeWait implements InterfaceBatchSizeWait{
  protected static final Logger LOGGER = LoggerFactory.getLogger(MaxBatchSizeWait.class);
  @ConfigProperty(name = "debezium.source.max.batch.size", defaultValue = CommonConnectorConfig.DEFAULT_MAX_BATCH_SIZE + "")
  int maxBatchSize;
  @ConfigProperty(name = "debezium.sink.batch.batch-size-wait.max-wait-ms", defaultValue = "300000")
  int maxWaitMs;
  @ConfigProperty(name = "debezium.sink.batch.batch-size-wait.wait-interval-ms", defaultValue = "10000")
  int waitIntervalMs;
//  @Inject
  DebeziumMetrics debeziumMetrics;

  /**
   *
   */
  @Override
  public void initizalize() {
    InterfaceBatchSizeWait.super.initizalize();
    assert waitIntervalMs < maxWaitMs : "`wait-interval-ms` cannot be bigger than `max-wait-ms`";

  }

  /**
   * @param numRecordsProcessed
   * @param processingTimeMs
   * @throws InterruptedException
   */
  @Override
  public void waitMs(long numRecordsProcessed, Integer processingTimeMs) throws InterruptedException {
    if (debeziumMetrics.snapshotRunning()) {
      return;
    }

    LOGGER.info("Processed {}, " +
            "QueueCurrentSize:{}, " +
            "QueueTotalCapacity:{}, " +
            "QueueCurrentUtilization:{}%, " +
            "MilliSecondsBehindSource:{}, " +
            "SnapshotCompleted:{}, " +
            "snapshotRunning:{}",
        numRecordsProcessed,
        debeziumMetrics.streamingQueueCurrentSize(), debeziumMetrics.maxQueueSize(),
        (debeziumMetrics.streamingQueueCurrentSize() / debeziumMetrics.maxQueueSize()) * 100,
        debeziumMetrics.streamingMilliSecondsBehindSource(),
        debeziumMetrics.snapshotCompleted(), debeziumMetrics.snapshotRunning()
    );

    int totalWaitMs = 0;
    while (totalWaitMs < maxWaitMs && debeziumMetrics.streamingQueueCurrentSize() < maxBatchSize) {
      totalWaitMs += waitIntervalMs;
      LOGGER.trace("Sleeping {} Milliseconds, QueueCurrentSize:{} < maxBatchSize:{}, Total wait {}",
          waitIntervalMs, debeziumMetrics.streamingQueueCurrentSize(), maxBatchSize, totalWaitMs);

      Thread.sleep(waitIntervalMs);
    }

    LOGGER.debug("Total wait {} Milliseconds, QueueCurrentSize:{}, maxBatchSize:{}",
        totalWaitMs, debeziumMetrics.streamingQueueCurrentSize(), maxBatchSize);
  }
}
