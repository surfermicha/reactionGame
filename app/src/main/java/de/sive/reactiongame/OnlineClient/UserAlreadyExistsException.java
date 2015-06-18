package de.sive.reactiongame.onlineClient;

/**
 * This Exception is thrown if the user to create already exists.
 */
public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }

    public UserAlreadyExistsException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UserAlreadyExistsException(String detailMessage) {
        super(detailMessage);
    }
}
