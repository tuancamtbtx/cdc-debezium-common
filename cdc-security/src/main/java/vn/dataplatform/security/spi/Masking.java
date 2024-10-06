package vn.dataplatform.security.spi;

public interface Masking {
    public String mailMasking(String email);
    public String passwordMasking(String password);

}
