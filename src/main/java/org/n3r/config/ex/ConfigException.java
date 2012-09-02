package org.n3r.config.ex;

public class ConfigException extends RuntimeException {

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
    

}
