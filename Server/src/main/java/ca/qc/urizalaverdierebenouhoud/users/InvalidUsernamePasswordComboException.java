package ca.qc.urizalaverdierebenouhoud.users;

/**
 * Exception thrown when the username and password combination is invalid.
 */
public class InvalidUsernamePasswordComboException extends Exception {
    public InvalidUsernamePasswordComboException(String message) {
        super(message);
    }
}
