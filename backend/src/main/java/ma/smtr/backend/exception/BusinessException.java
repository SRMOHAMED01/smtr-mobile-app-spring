package ma.smtr.backend.exception;

/**
 * Levée quand une règle métier n'est pas respectée
 * (ex : document absent sans justification, km retour < km départ...).
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
