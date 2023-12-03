package vn.dataplatform.cdc.sink.bigquery;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.LegacySQLTypeName;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.protobuf.Descriptors;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONObject;
import vn.dataplatform.cdc.sinks.bigquery.BqToBqStorageSchemaConverter;

/**
 * @author tuan.nguyen3
 */
public class ManualTestStreamLoading {

  public static void main(String[] args) throws IOException, InterruptedException, Descriptors.DescriptorValidationException, ExecutionException {
    // [START ]
    // Table schema definition
    BigQuery bigquery = BigQueryOptions.newBuilder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .setLocation("EU")
        .build()
        .getService();
    TableName tn = TableName.of(bigquery.getOptions().getProjectId(), "tuan_test", "test_json_loading_stream");
    TableId tableId = TableId.of(tn.getDataset(), tn.getTable());
    Field[] fields =
        new Field[]{
            Field.of("c_id", LegacySQLTypeName.INTEGER),
            Field.of("c_date", LegacySQLTypeName.DATE),
            Field.of("c_datetime", LegacySQLTypeName.DATETIME),
            Field.of("c_ts", LegacySQLTypeName.TIMESTAMP)
        };
    // Table schema definition
    Schema schema = Schema.of(fields);
    TableInfo tableInfo = TableInfo.newBuilder(tableId, StandardTableDefinition.newBuilder()
        .setSchema(schema)
        .build()).build();
    //bigquery.delete(tableId);
    bigquery.create(tableInfo);
    System.out.println("Created table!");

    BigQueryWriteSettings bigQueryWriteSettings = BigQueryWriteSettings
        .newBuilder()
        .setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.getApplicationDefault()))
        .build();
    BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create(bigQueryWriteSettings);

    Thread.sleep(5000);
    JsonStreamWriter streamWriter = JsonStreamWriter
        .newBuilder(tn.toString(), BqToBqStorageSchemaConverter.convertTableSchema(schema), bigQueryWriteClient)
        .build();

    JSONArray jsonArr = new JSONArray();
    JSONObject record = new JSONObject();
    record.put("c_id", 1);
    record.put("c_date", 3);
    record.put("c_datetime", 1662805921200L);
    record.put("c_ts", 1662805921200L);
    jsonArr.put(record);

    ApiFuture<AppendRowsResponse> request = streamWriter.append(jsonArr);
    //AppendRowsResponse waitComplete = request.get();
    System.out.println("DONE");
    //streamWriter.close();
    TableResult result =
        bigquery.query(QueryJobConfiguration.newBuilder("select * from " + tableId.getDataset() + "." + tableId.getTable()).build());
    result.getValues().forEach(r -> System.out.println(r.toString()));
    // [END ]
  }
}
