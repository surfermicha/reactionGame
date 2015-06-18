package de.sive.reactiongame;

/**
 * This class holds the information about the result of a mini game. It's normally used to pass the data to the Async-Task
 *
 * @author Michael Landreh
 */
public class GameResult {
    private int playerNumber;
    private boolean isWinnner;
    private int selectedAnswerIndex;

    public GameResult(int playerNumber, int selectedAnswerIndex, boolean isWinnner) {
        this.playerNumber = playerNumber;
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.isWinnner = isWinnner;
    }

    public int getSelectedAnswer() {
        return selectedAnswerIndex;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswerIndex = selectedAnswerIndex;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public boolean isWinnner() {
        return isWinnner;
    }

    public void setIsWinnner(boolean isWinnner) {
        this.isWinnner = isWinnner;
    }
}
