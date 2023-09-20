package vn.dataplatform.cdc.transform;

import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tuan.nguyen3
 */
public class TestTextCryptoTransform {
    private static final Logger log = LoggerFactory.getLogger(TestTextCryptoTransform.class);
    private static final Map<String, Object> props = new HashMap<>();

    @BeforeAll
    public static void setup() {
        props.put("type", "vn.dataplatform.cdc.transform.EncryptTransformer");
        props.put("fields", "contacts, code,bank_account.id, bank_account.info.email,info_str.fullName, info_str.company_info.phones");
        props.put("contacts.provider", "com.ts.datalake.security.provider.crypto.TinkDaeadEncryptorProvider");
        props.put("code.provider", "com.ts.datalake.security.provider.crypto.TinkDaeadEncryptorProvider");
        props.put("bank_account.id.provider", "com.ts.datalake.security.provider.crypto.TinkDaeadEncryptorProvider");
        props.put("bank_account.info.email.provider", "com.ts.datalake.security.provider.crypto.TinkDaeadEncryptorProvider");
        props.put("info_str.fullName.provider", "com.ts.datalake.security.provider.crypto.TinkDaeadEncryptorProvider");
        props.put("info_str.company_info.phones.provider", "com.ts.datalake.security.provider.crypto.TinkDaeadEncryptorProvider");

    }

    @Test
    public void Test_ValueFlatEncrypt() {
        Map<String, Object> values = new HashMap<>();
        values.put("phone", "0123456678");
        values.put("code", "abc");
        values.put("contacts", new ArrayList<Object>() {
            {
                add(11919);
                add("for");
                add("Geeks");
            }
        });
        values.put("bank_account", new HashMap<String, Object>() {{
            put("id", "BANK_CODE");
            put("info", new HashMap<String, Object>() {{
                put("name", "tuan.nguyen3");
                put("email", "tuan.nguyen3@trustingsocial.com");
            }});
        }});
        values.put("info_str", "{\"fullName\":\"tuancam\",\"company_info\":{\"phones\":[16262882,181881], ,\"email\":\"tuan.nguyen3@trustingsocial.com\"}}");
        Encrypt.Value<SourceRecord> encrypt = new Encrypt.Value<>();
        encrypt.configure(props);
        SourceRecord record = new SourceRecord(null, null, "test", 0,
                null, values);
        SourceRecord transformedRecord = encrypt.apply(record);
        log.info("\n - Crypto value: {}", transformedRecord.value());
        Assertions.assertTrue(true);
    }

//    @Test
    public void Test_ValueNestedJsonEncrypt() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "tuan.nguyen3");
        values.put("phone", "0123456678");
        values.put("code", "abc");
        values.put("bank_account", new HashMap<String, Object>() {{
            put("id", "BANK_CODE");
        }});
        Encrypt.Value<SourceRecord> encrypt = new Encrypt.Value<>();
        encrypt.configure(props);
        SourceRecord record = new SourceRecord(null, null, "test", 0,
                null, values);
        SourceRecord transformedRecord = encrypt.apply(record);
        System.out.println("record: " + record);
        System.out.println("transformer: " + transformedRecord.value());
        Assertions.assertTrue(true);
    }
    @Test
    public void Test_JsonPath(){
        Map<String,Object> values = new HashMap<>();
        values.put("phone", "0123456678");
        values.put("bank_account", new HashMap<String, Object>() {{
            put("id", "BANK_CODE");
            put("info", new HashMap<String, Object>() {{
                put("name", "tuan.nguyen3");
                put("email", "tuan.nguyen3@trustingsocial.com");
            }});
        }});
        String jsonStr = "{\"fullName\":\"tuancam\",\"company_info\":{\"phones\":[16262882,181881]},\"email\":\"tuan.nguyen3@trustingsocial.com\"}";
        values.put("info_str", jsonStr);
        Map<String, Object> tmp = new HashMap<>(values);
        Object a = JsonPath.parse(values).read("$.info_str");
        log.info("JsonPath: {}", a);
        Assertions.assertTrue(true);
    }
}
