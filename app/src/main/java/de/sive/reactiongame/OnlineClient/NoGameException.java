package de.sive.reactiongame.onlineClient;

/**
 * Created by Michael on 23.05.2015.
 */
public class NoGameException extends Exception {
    public NoGameException(String message) {
        super(message);
    }

    public NoGameException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
