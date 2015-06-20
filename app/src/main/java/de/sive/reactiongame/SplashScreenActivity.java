package de.sive.reactiongame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import de.sive.reactiongame.mainActivity.MainActivity;
import de.sive.reactiongame.onlineClient.OnlineServiceClient;
import de.sive.reactiongame.session.LoginActivity;


public class SplashScreenActivity extends Activity {
    private static final int SESSION_NOT_FOUND_CODE = -1;
    private static final String TAG = SplashScreenActivity.class.getName();

    private SessionStatusTask mSessionStatusTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Get Session key
        int sessionId = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_SESSION_ID_KEY, SESSION_NOT_FOUND_CODE);
        Log.d(TAG, "Session ID from prefs is: " + sessionId);
        if (sessionId == SESSION_NOT_FOUND_CODE) {
            Log.d(TAG, "session not found in prefs.");
            //No session available. The User is redirected to login page
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

        //Start Async Task to check wether the stored session ID is valid.
        mSessionStatusTask = new SessionStatusTask(this, sessionId);
        mSessionStatusTask.execute();
    }

    private void checkServerSessionResponse(boolean serverResponse) {
        Log.d(TAG, "serverresponse is: " + serverResponse);
        if (serverResponse) {
            Log.d(TAG, "User still has a valid session. Skip Login...");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "Server answer: session is not valid.");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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

    public class SessionStatusTask extends AsyncTask<Void, Void, Boolean> {

        private int mSessionId = SESSION_NOT_FOUND_CODE;
        private Context context;

        SessionStatusTask(Activity thisActivity, int sessionId) {
            context = thisActivity.getApplicationContext();
            mSessionId = sessionId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OnlineServiceClient remote = new OnlineServiceClient();
            try {
                // Ask server whether a user session with the saved session ID is available
                Log.d(TAG, "Check whether session ID is valid... sessionId: " + mSessionId);
                boolean serverSessionAvailable = remote.isSessionAvailable(mSessionId);
                if (serverSessionAvailable) {
                    return true;
                }
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            checkServerSessionResponse(result);
        }

        @Override
        protected void onCancelled(Boolean result) {
            checkServerSessionResponse(result);
        }


    }
}
