package de.sive.reactiongame.onlineClient;

/**
 * This Exception is thrown is the server request to get a game fails.
 * @author Michael Landreh
 */
public class NoGameException extends Exception {
    public NoGameException(String message) {
        super(message);
    }

    public NoGameException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
