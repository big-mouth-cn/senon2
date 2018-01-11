package org.bigmouth.senon.commom.registry;

/**
 * <h3>registry exception</h3>
 *
 * @author allen
 * @since 1.0.0
 */
public class RegistryException extends RuntimeException {

    public RegistryException() {
        super();
    }

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }

    protected RegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
