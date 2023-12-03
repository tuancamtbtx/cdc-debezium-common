package vn.dataplatform.cdc.sinks.bigquery.batchsizewait;

import static io.debezium.config.CommonConnectorConfig.DEFAULT_MAX_BATCH_SIZE;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import java.util.LinkedList;
import java.util.LongSummaryStatistics;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tuan.nguyen3
 */
@Dependent
@Named("DynamicBatchSizeWait")
public class DynamicBatchSizeWait implements InterfaceBatchSizeWait{
  protected static final Logger LOGGER = LoggerFactory.getLogger(DynamicBatchSizeWait.class);
  final LinkedList<Long> batchSizeHistory = new LinkedList<>();
  final LinkedList<Long> sleepMsHistory = new LinkedList<>();
  @ConfigProperty(name = "debezium.source.max.batch.size", defaultValue = DEFAULT_MAX_BATCH_SIZE + "")
  Integer maxBatchSize;
  @ConfigProperty(name = "debezium.sink.batch.batch-size-wait.max-wait-ms", defaultValue = "300000")
  Integer maxWaitMs;
  /**
   *
   */
  @Override
  public void initizalize() {
    InterfaceBatchSizeWait.super.initizalize();
    batchSizeHistory.add(1L);
    batchSizeHistory.add(1L);
    batchSizeHistory.add(1L);
    sleepMsHistory.add(100L);
    sleepMsHistory.add(100L);
    sleepMsHistory.add(100L);
  }
  private double getAverage(LinkedList<Long> linkedList) {
    LongSummaryStatistics stats = linkedList.stream()
        .mapToLong((x) -> x)
        .summaryStatistics();
    return stats.getAverage();
  }

  public long getWaitMs(long numRecords) {
    batchSizeHistory.add(numRecords);
    batchSizeHistory.removeFirst();
    long sleepMs = 1;

    // if batchsize > XX% decrease wait
    if ((getAverage(batchSizeHistory) / maxBatchSize) >= 0.97) {
      sleepMs = (long) (sleepMsHistory.getLast() * 0.50);
    }
    // if batchsize > XX% decrease wait
    else if ((getAverage(batchSizeHistory) / maxBatchSize) >= 0.95) {
      sleepMs = (long) (sleepMsHistory.getLast() * 0.65);
    }
    // if batchsize > XX% decrease wait
    else if ((getAverage(batchSizeHistory) / maxBatchSize) >= 0.90) {
      sleepMs = (long) (sleepMsHistory.getLast() * 0.80);
    } else if ((getAverage(batchSizeHistory) / maxBatchSize) >= 0.85) {
      return sleepMsHistory.getLast();
    }
    // else increase
    else {
      sleepMs = (sleepMsHistory.getLast() * maxBatchSize) / numRecords;
    }

    sleepMsHistory.add(Math.min(Math.max(sleepMs, 100), maxWaitMs));
    sleepMsHistory.removeFirst();

    return sleepMsHistory.getLast();
  }
  /**
   * @param numRecordsProcessed
   * @param processingTimeMs
   * @throws InterruptedException
   */
  @Override
  public void waitMs(long numRecordsProcessed, Integer processingTimeMs) throws InterruptedException {
    long sleepMs = Math.max(getWaitMs(numRecordsProcessed) - processingTimeMs, 0);
    if (sleepMs > 2000) {
      LOGGER.debug("Waiting {} ms", sleepMs);
      Thread.sleep(sleepMs);
    }
  }
}
