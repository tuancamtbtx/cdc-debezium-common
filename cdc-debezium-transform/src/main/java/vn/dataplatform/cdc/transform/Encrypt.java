package vn.dataplatform.cdc.transform;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;

/**
 * @author tuan.nguyen3
 */
public abstract class Encrypt<R extends ConnectRecord<R>> extends Crypto<R> {
    @Override
    protected String crypto(String providerClass, String text) throws Exception {
        return this.getEncryptor(providerClass).encrypt(text);
    }

    public static class Key<R extends ConnectRecord<R>> extends Encrypt<R> {
        @Override
        protected Schema operatingSchema(R record) {
            return record.keySchema();
        }

        @Override
        protected Object operatingValue(R record) {
            return record.key();
        }

        @Override
        protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), updatedSchema, updatedValue, record.valueSchema(), record.value(), record.timestamp());
        }
    }

    public static class Value<R extends ConnectRecord<R>> extends Encrypt<R> {
        @Override
        protected Schema operatingSchema(R record) {
            return record.valueSchema();
        }

        @Override
        protected Object operatingValue(R record) {
            return record.value();
        }

        @Override
        protected R newRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), updatedSchema, updatedValue, record.timestamp());
        }
    }

}
