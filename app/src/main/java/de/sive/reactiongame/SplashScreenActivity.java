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
    private boolean mServerSessionValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Get Session key
        int sessionId = PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_SESSION_ID_KEY, SESSION_NOT_FOUND_CODE);
        if (sessionId == SESSION_NOT_FOUND_CODE) {
            //No session available. The User is redirected to login page
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //Start Async Task to check wether the stored session ID is valid.
        mSessionStatusTask = new SessionStatusTask(this, sessionId);
        mSessionStatusTask.execute();

        if (mServerSessionValid) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
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

    public class SessionStatusTask extends AsyncTask<Void, Void, Void> {

        private final int mSessionId = SESSION_NOT_FOUND_CODE;
        private Context context;

        SessionStatusTask(Activity thisActivity, int sessionId) {
            context = thisActivity.getApplicationContext();

        }

        @Override
        protected Void doInBackground(Void... params) {
            OnlineServiceClient remote = new OnlineServiceClient();
            try {
                // Ask server whether a user session with the saved sessin ID is available
                boolean serverSessionAvailable = remote.isSessionAvailable(mSessionId);
                if (serverSessionAvailable) {
                    mServerSessionValid = true;
                }
            } catch (Exception e) {
                Log.w(TAG, e.getMessage());
                mServerSessionValid = false;
            }
            mServerSessionValid = false;
            return null;
        }


    }
}
