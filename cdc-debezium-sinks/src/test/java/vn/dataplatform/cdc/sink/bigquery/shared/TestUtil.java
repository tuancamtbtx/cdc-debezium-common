package vn.dataplatform.cdc.sink.bigquery.shared;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import java.security.SecureRandom;
import org.apache.kafka.connect.source.SourceRecord;

/**
 * @author tuan.nguyen3
 */
public class TestUtil {
  static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static final SecureRandom rnd = new SecureRandom();


  public static int randomInt(int low, int high) {
    return rnd.nextInt(high - low) + low;
  }

  public static String randomString(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++)
      sb.append(AB.charAt(rnd.nextInt(AB.length())));
    return sb.toString();
  }

  public static DebeziumEngine.RecordCommitter<ChangeEvent<Object, Object>> getCommitter() {
    return new DebeziumEngine.RecordCommitter() {
      public synchronized void markProcessed(SourceRecord record) throws InterruptedException {
        return;
      }

      @Override
      public void markProcessed(Object record) throws InterruptedException {
        return;
      }

      public synchronized void markBatchFinished() throws InterruptedException {
        return;
      }

      @Override
      public void markProcessed(Object record, DebeziumEngine.Offsets sourceOffsets) throws InterruptedException {
        return;
      }

      @Override
      public DebeziumEngine.Offsets buildOffsets() {
        return null;
      }
    };
  }
}
