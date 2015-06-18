package de.sive.reactiongame.onlineClient;

/**
 * This Exception is thrown if the game result coudn't be pushed to the server.
 *
 * @author Michael Landreh
 */
public class ResultNotPushedException extends Exception {
    public ResultNotPushedException(String message) {
        super(message);
    }

    public ResultNotPushedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
