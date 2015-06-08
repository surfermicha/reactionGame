package GameServer;

import android.util.Log;

import java.util.Random;

/**
 * Created by Michael on 03.04.2015.
 */
public class GameSmallestNumber implements IGame {
    //Log-Tag
    private static final String TAG = "GameSmallestNumber";

    //Constants
    private static final int DEFAULT_GAME_DURATION = 10000;


    //Global variables
    private int gameDuration;
    private String[] answers = new String[4];
    private int correctAnswerIndex;


    //Constructors
    //Empty constructor generates a random game
    public GameSmallestNumber() {
        int min = 100;
        Random randomGenerator = new Random();
        for (int i = 0; i <= 3; i++) {
            boolean numberIsNotUnique = true;
            int number = 0;
            //Generate unique random number
            while (numberIsNotUnique) {
                number = randomGenerator.nextInt(100);

                checkAnswer:
                for (int j = 0; j <= i; j++) {
                    if (answers[j] != null) {
                        if (answers[j].equals(Integer.toString(number)) == false) {
                            numberIsNotUnique = false;

                        } else {
                            Log.d(TAG, "The tried number is not unique. We'll try to find another one.");
                            numberIsNotUnique = true;
                            break checkAnswer;
                        }
                    } else {
                        numberIsNotUnique = false;
                    }
                }
            }


            if (number < min) {
                min = number;
                correctAnswerIndex = i;
                Log.d(TAG, "new correct answer index was set to " + correctAnswerIndex);
            }
            answers[i] = Integer.toString(number);
        }
        gameDuration = DEFAULT_GAME_DURATION;

    }

    public GameSmallestNumber(String[] answers, int correctAnswerIndex) {
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.gameDuration = DEFAULT_GAME_DURATION;
    }

    public GameSmallestNumber(String[] answers, int correctAnswerIndex, int gameDuration) {
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.gameDuration = gameDuration;
    }

    //Public methods
    @Override
    public int getGameDurationInMilliseconds() {
        return gameDuration;
    }

    public String[] getAnswers() {
        return answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getCorrectAnswer() {
        return answers[correctAnswerIndex];
    }
}

