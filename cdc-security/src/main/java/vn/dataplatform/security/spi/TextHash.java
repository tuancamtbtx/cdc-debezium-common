package vn.dataplatform.security.spi;

/**
 * @author tuan.nguyen3
 */
public interface TextHash {
    public String hash(String text) throws Exception;
    void initialize() throws Exception;
}
