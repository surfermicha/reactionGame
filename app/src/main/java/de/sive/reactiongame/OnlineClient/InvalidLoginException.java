package de.sive.reactiongame.onlineClient;

/**
 * This Exception is thrown if the login process fails
 *
 * @author Michael Landreh
 */
public class InvalidLoginException extends Exception {
    //variables
    private int errorCode;

    //Constructors
    public InvalidLoginException() {
    }

    public InvalidLoginException(String detailMessage, int errorCode) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public InvalidLoginException(String message) {
        super(message);
    }

    //Getter and Setter

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
