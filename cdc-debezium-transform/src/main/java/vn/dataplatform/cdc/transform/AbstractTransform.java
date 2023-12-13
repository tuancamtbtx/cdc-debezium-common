package vn.dataplatform.cdc.transform;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

import com.jayway.jsonpath.JsonPath;
import io.debezium.config.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.dataplatform.cdc.utils.TransformUtils;

/**
 * @author tuan.nguyen3
 */
public abstract class AbstractTransform<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final ConfigDef CONFIG_DEF =
        new ConfigDef().define(TransformConfig.FIELD_FOR_CRYPTO, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH,
            "Field name for list field name need to encrypt");
    protected static final String PURPOSE = "adding crypto to record";
    private static final io.debezium.config.Field FIELD_FOR_CRYPTO;
    private final static Logger log = LoggerFactory.getLogger(AbstractTransform.class);

    static {
        FIELD_FOR_CRYPTO = io.debezium.config.Field.create("fields").withDisplayName("Field for crypto").withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM).withImportance(ConfigDef.Importance.HIGH)
            .withDescription("Field name for list field name need to encrypt/decrypt");
    }

    protected String encryptedFields;
    protected Cache<Schema, Schema> schemaUpdateCache;
    protected Configuration config;

    protected Optional<String> getCryptoProvider(String field) {
        String classProvider = this.config.getString(getFieldForCryptoProvider(field + "." + TransformConfig.PROVIDER_FOR_CRYPTO));
        if (TransformUtils.isNotEmpty(classProvider)) {
            return Optional.of(classProvider.replaceAll("\\s", ""));
        }
        return Optional.empty();
    }

    protected Object encryptByDataType(String providerClass, Object value) throws Exception {
        if (value instanceof String) {
            return this.crypto(providerClass, (String) value);
        } else if (value instanceof List) {
            ArrayList<String> arrStr = TransformUtils.jsonStringToArray(value.toString());
            ArrayList<String> arrEncrypted = new ArrayList<>();
            for (String str : arrStr) {
                arrEncrypted.add(this.crypto(providerClass, str));
            }
            return arrEncrypted;
        } else {
            return value;
        }
    }

    private io.debezium.config.Field getFieldForCryptoProvider(String name) {
        return io.debezium.config.Field.create(name).withDisplayName("Field for crypto provider name").withType(ConfigDef.Type.STRING)
            .withWidth(ConfigDef.Width.MEDIUM).withImportance(ConfigDef.Importance.HIGH)
            .withDescription("Field name for crypto provider to encrypt/decrypt");
    }

    @Override
    public R apply(R record) {
        if (operatingSchema(record) == null) {
            return applySchemaless(record);
        } else {
            return applyWithSchema(record);
        }
    }

    protected Map<String, Object> processDataNestedWithSchemaless(String field, Map<String, Object> updatedValue, String providerClass) {
        Optional<String> rootField = TransformUtils.getRootField(field);
        if (rootField.isEmpty()) {
            return updatedValue;
        }
        Optional<String> pathChildField = TransformUtils.getPathChildField(field);
        if (pathChildField.isEmpty()) {
            return updatedValue;
        }
        Object rootValue = updatedValue.get(rootField.get().trim());
        Optional<Object> valueNeedEncrypt = TransformUtils.getValueFromJsonObject(rootValue, pathChildField.get());
        try {
            if (valueNeedEncrypt.isPresent()) {
                Object encrypted = this.encryptByDataType(providerClass, valueNeedEncrypt.get());
                if (rootValue instanceof String) {
                    String encryptedStr = JsonPath.parse(rootValue.toString()).set(pathChildField.get(), encrypted).jsonString();
                    updatedValue.put(rootField.get(), encryptedStr);
                } else {
                    JsonPath.parse(rootValue).set(pathChildField.get(), encrypted);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return updatedValue;
    }


    protected Map<String, Object> processDataFlattenWithSchemaless(String field, Map<String, Object> updatedValue, String providerClass) {
        try {
            Object valueNeedEncrypt = JsonPath.parse(updatedValue).read(field);
            Object encrypted = this.encryptByDataType(providerClass, valueNeedEncrypt);
            updatedValue.put(field, encrypted);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updatedValue;

    }


    protected Struct processDataFlattenWithSchema(String fieldName, Struct value, Struct updatedValue, String providerClass) {
        try {
            Object valueNeedEncrypt = value.get(fieldName);
            Object encrypted = this.encryptByDataType(providerClass, valueNeedEncrypt);
            updatedValue.put(fieldName, encrypted);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updatedValue;
    }

    protected Struct processDataNestedWithSchema(String field, Struct value, Struct updatedValue, String providerClass) {
        try {
            Optional<String> rootField = TransformUtils.getRootField(field);
            if (rootField.isPresent() && value.schema().field(rootField.get()) != null) {
                // keep value is changed
                Object valueInUpdated = updatedValue.get(rootField.get().trim());
                Object rootValue = valueInUpdated != null ? valueInUpdated : value.get(rootField.get().trim());
                Optional<String> pathChildField = TransformUtils.getPathChildField(field);
                if (pathChildField.isPresent()) {
                    Optional<Object> valueNeedEncrypt = TransformUtils.getValueFromJsonObject(rootValue, pathChildField.get());
                    if (valueNeedEncrypt.isEmpty()) {
                        return updatedValue;
                    }
                    Object encrypted = this.encryptByDataType(providerClass, valueNeedEncrypt.get());
                    String encryptedStr = JsonPath.parse(rootValue.toString()).set(pathChildField.get(), encrypted).jsonString();
                    updatedValue.put(rootField.get(), encryptedStr);
                }
            }
            return updatedValue;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return updatedValue;
    }

    private R applySchemaless(R record) {
        Map<String, Object> value = requireMap(operatingValue(record), PURPOSE);
        Map<String, Object> updatedValue = new HashMap<>(value);
        List<String> fields = TransformUtils.getFields(this.encryptedFields);
        if (fields.isEmpty()) {
            return record;
        }
        for (String field : fields) {
            Optional<String> providerClass = this.getCryptoProvider(field);
            if (providerClass.isPresent()) {
                if (TransformUtils.isNestedField(field)) {
                    updatedValue = processDataNestedWithSchemaless(field, updatedValue, providerClass.get());
                } else if (value.containsKey(field) && !TransformUtils.isNestedField(field)) {
                    updatedValue = processDataFlattenWithSchemaless(field, updatedValue, providerClass.get());
                } else {
                    log.info("Field {} not found in record", field);
                }
            }
        }
        return newRecord(record, null, updatedValue);
    }

    private R applyWithSchema(R record) {
        final Struct value = requireStruct(operatingValue(record), PURPOSE); // check value in record is struct - not struct right is throw exception
        List<String> fields = TransformUtils.getFields(this.encryptedFields);
        if (fields.isEmpty()) { // encrypted fields is empty, so we don't need to process
            return record;
        }
        Schema updatedSchema = schemaUpdateCache.get(value.schema());
        if (updatedSchema == null) {
            updatedSchema = makeUpdateSchema(value.schema(), fields);
            schemaUpdateCache.put(value.schema(), updatedSchema);
        }
        Struct updatedValue = makeCopy(value, updatedSchema, fields);
        for (String fieldName : fields) {
            Optional<String> providerClass = this.getCryptoProvider(fieldName);
            if (providerClass.isPresent()) {
                if (TransformUtils.isNestedField(fieldName)) {
                    updatedValue = processDataNestedWithSchema(fieldName, value, updatedValue, providerClass.get());
                } else if (value.schema().field(fieldName) != null && !TransformUtils.isNestedField(fieldName)) {
                    updatedValue = processDataFlattenWithSchema(fieldName, value, updatedValue, providerClass.get());
                } else {
                    log.info("Field {} not found in record", fieldName);
                }
            }
        }
        return newRecord(record, updatedSchema, updatedValue);
    }

    private Struct makeCopy(Struct value, Schema updatedSchema, List<String> listFieldNames) {
        final Struct updatedValue = new Struct(updatedSchema);
        for (Field field : value.schema().fields()) {
            if (!listFieldNames.contains(field.name())) {
                updatedValue.put(field, value.get(field));
            }
        }
        return updatedValue;
    }

    private Schema makeUpdateSchema(Schema schema, List<String> fieldNames) {
        final SchemaBuilder builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());
        for (Field field : schema.fields()) {
            if (fieldNames.contains(field.name())) {
                /*
                 * currently, we only support encrypt string type
                 * so if field is array type, we will convert to array string type
                 */
                if (field.schema().type() == Schema.Type.ARRAY) {
                    builder.field(field.name(), SchemaBuilder.array(Schema.OPTIONAL_STRING_SCHEMA).optional().build());
                } else {
                    builder.field(field.name(), Schema.STRING_SCHEMA);
                }
            } else {
                builder.field(field.name(), field.schema());
            }
        }
        return builder.build();
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        this.config = Configuration.from(configs);
        String encryptedFields = config.getString(FIELD_FOR_CRYPTO);
        if (encryptedFields != null && !encryptedFields.trim().isEmpty()) {
            this.encryptedFields = encryptedFields.replaceAll("\\s", "");
        }
        log.info("transformation with encrypted fields are: {}", this.encryptedFields);
        schemaUpdateCache = new SynchronizedCache<>(new LRUCache<>(16));
    }

    @Override
    public void close() {
        // No resources to clean up
        schemaUpdateCache = null;
    }

    protected abstract String crypto(String providerClass, String text) throws Exception;

    protected abstract Schema operatingSchema(R record);

    protected abstract Object operatingValue(R record);

    protected abstract R newRecord(R record, Schema updatedSchema, Object updatedValue);

}
