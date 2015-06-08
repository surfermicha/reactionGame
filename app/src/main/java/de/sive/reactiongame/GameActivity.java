package de.sive.reactiongame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import GameServer.GameSmallestNumber;
import de.sive.reactiongame.onlineClient.NoGameException;
import de.sive.reactiongame.onlineClient.OnlineServiceClient;
import de.sive.reactiongame.tasks.CreateGameTask;


public class GameActivity extends ActionBarActivity {
    //Constants
    private static final String TAG = "GameActivity"; //Log-Tag

    //Class variables
    GameServer.GameSmallestNumber thisGame;
    CountDownTimer gameTimer;
    boolean isGameTimerCanceled = false;
    Button btnAnswer1;
    Button btnAnswer2;
    Button btnAnswer3;
    Button btnAnswer4;
    TextView txtCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupGame();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    This method setups the view for a new smallest number game with the data recieved from the server.
    @author Michael Landreh
     */
    private void setupGame() {

        btnAnswer1 = (Button) findViewById(R.id.button_answer1);
        btnAnswer2 = (Button) findViewById(R.id.button_answer2);
        btnAnswer3 = (Button) findViewById(R.id.button_answer3);
        btnAnswer4 = (Button) findViewById(R.id.button_answer4);
        txtCountDown = (TextView) findViewById(R.id.txtCountDown);


        //TODO: Receive Data from server async
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //if (networkInfo != null && networkInfo.isConnected()) {
        Log.d(TAG, "Started to download game data...");

        CreateGameTask createGameTask = new CreateGameTask(this);
        createGameTask.execute();

        //} else {
        //    Toast.makeText(this,"No internet connection available", Toast.LENGTH_LONG).show();
        //    Log.w(TAG, "Loading game was canceled because of a missing internet connection.");
        //}


    }

    /*
   This is the timer which controls the game duration. Every second it refreshes the activity with the remaining time and
   calls the finishgame() method is the timer runs out.
   use the GAME_DURATION constant to set the game duration.
   @author Michael Landreh
    */
    private CountDownTimer setGameTimer(int gameDurationInMilliseconds) {

        CountDownTimer timer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtCountDown.setText("" + millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                if (!isGameTimerCanceled)
                    finishGame(null);
            }
        }.start();
        return timer;
    }




    /*
    The callback method which is calles when one of the answer buttons is pressed.
    @params view    The reference of the calling view
    @author Michael Landreh
     */

    public void onAnswerButtonPressed(View view) {
        //Check which button is pressed
        Button pressedButton = null;
        switch (view.getId()) {
            case R.id.button_answer1:
                pressedButton = btnAnswer1;
                break;
            case R.id.button_answer2:
                pressedButton = btnAnswer2;
                break;
            case R.id.button_answer3:
                pressedButton = btnAnswer3;
                break;
            case R.id.button_answer4:
                pressedButton = btnAnswer4;
                break;
            default:
                pressedButton = null;
                break;
        }
        //Abort the timer
        gameTimer.cancel();
        isGameTimerCanceled = true;
        //finish game and calculate the result
        finishGame(pressedButton);
    }

    /*
    This method finishes the current game and gives the user feedback about the result.
    If no pressed Button is given in the parameters the method assumes that its a timeout.
    @params pressedButton   A reference to the button that was pressed by the user.
    @author Michael Landreh
     */
    private void finishGame(Button pressedButton) {
        //Disable answer buttons
        btnAnswer1.setEnabled(false);
        btnAnswer2.setEnabled(false);
        btnAnswer3.setEnabled(false);
        btnAnswer4.setEnabled(false);

        //Mark correct answer
        getCorrectAnswerButton().setTextColor(Color.GREEN);

        //Checks whether the user has clicked a button
        if (pressedButton != null) {
            //Button-Click
            if (pressedButton == getCorrectAnswerButton()) {
                Log.d(TAG, "The user's answer is correct.");
                txtCountDown.setText(R.string.smNumber_GameFinished_AnswerCorrect);
            } else {
                Log.d(TAG, "The user's answer is wrong.");
                pressedButton.setTextColor(Color.RED);
                txtCountDown.setText(getString(R.string.smNumber_GameFinished_AnswerWrong));
            }
        } else {
            //Game-Timeout
            txtCountDown.setText(getString(R.string.smNumber_GameFinished_Timeout));
            Log.d(TAG, "Time is running out and the user has no answer chosen.");

        }
        //TODO: Report result to the server async
        Log.d(TAG, "The game was finished and the answers were checked.");
    }

    /*
    This method returns a reference to the button that represents the correct answer
    @return The button object that represents the correct answer
    @author Michael Landreh
     */
    private Button getCorrectAnswerButton() {
        switch (thisGame.getCorrectAnswerIndex()) {
            case 0:
                return btnAnswer1;
            case 1:
                return btnAnswer2;
            case 2:
                return btnAnswer3;
            case 3:
                return btnAnswer4;
            default:
                return null;
        }
    }


    /**
     * Created by Michael on 23.05.2015.
     */
    public class CreateGameTask extends AsyncTask<String, Integer, GameSmallestNumber> {

        private static final String TAG = "CreateGameTask";
        private ProgressDialog dialog;
        private Context context;

        public CreateGameTask(Activity thisActivity) {
            dialog = new ProgressDialog(thisActivity);
            context = thisActivity.getApplicationContext();
        }

        protected void onPreExecute() {
            dialog.setMessage("Loading Questions...");
            dialog.show();
        }

        protected GameSmallestNumber doInBackground(String... params) {
            OnlineServiceClient remote = new OnlineServiceClient();
            String[] testanswers = {"1", "2", "3", "4"};
            GameSmallestNumber game = null;
            try {
                game = remote.createGame(testanswers);
            } catch (NoGameException e) {
                cancel(true);
                Log.e(TAG, "The game couldn't be loaded from server. Error Message: " + e.getMessage());
                game = null;

            }

            return game;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(GameSmallestNumber game) {
            //Dismiss the ProgressDialog if it's showing
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (game == null) {
                thisGame = null;
                Toast.makeText(context, "The game couldn't be loaded.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "The recieved game data is emtpy.");

            } else {
                thisGame = game;
                Toast.makeText(context, "The game was successfully loaded.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Game was recieved. Setting up UI...");
                //Fills the button texts with the answers that are recieved from the server
                //thisGame = new GameSmallestNumber();
                btnAnswer1.setText(thisGame.getAnswers()[0]);
                btnAnswer2.setText(thisGame.getAnswers()[1]);
                btnAnswer3.setText(thisGame.getAnswers()[2]);
                btnAnswer4.setText(thisGame.getAnswers()[3]);
                Log.d(TAG, "A new game was created and served to the activity.");
                gameTimer = setGameTimer(thisGame.getGameDurationInMilliseconds());

            }

        }

        protected void onCancelled() {
            //Dismiss the ProgressDialog if it's showing
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(context, "The game couldn't be loaded.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Async create game task was cancelled.");

        }

    }
}
