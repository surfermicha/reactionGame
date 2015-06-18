package de.sive.reactiongame.mainActivity;

import java.security.Timestamp;
import java.util.Date;

/**
 * This class represents a single match item in the Listview of all matches
 *
 * @author Michael Landreh
 */
public class GameRow {
    private String opponent;
    private String currentMiniGame;
    private String gameStatus;
    private int pointsOpponent;
    private int pointsSelf;
    private Timestamp lastActionTime;

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getCurrentMiniGame() {
        return currentMiniGame;
    }

    public void setCurrentMiniGame(String currentMiniGame) {
        this.currentMiniGame = currentMiniGame;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getPointsOpponent() {
        return pointsOpponent;
    }

    public void setPointsOpponent(int pointsOpponent) {
        this.pointsOpponent = pointsOpponent;
    }

    public int getPointsSelf() {
        return pointsSelf;
    }

    public void setPointsSelf(int pointsSelf) {
        this.pointsSelf = pointsSelf;
    }

    public Timestamp getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(Timestamp lastActionTime) {
        this.lastActionTime = lastActionTime;
    }
}
