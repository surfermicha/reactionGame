package de.sive.reactiongame.mainActivity;

import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import de.sive.reactiongame.PrefUtils;
import de.sive.reactiongame.R;
import de.sive.reactiongame.SplashScreenActivity;
import de.sive.reactiongame.onlineClient.OnlineServiceClient;
import de.sive.reactiongame.session.SessionDataUtils;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, GamesFragment.OnFragmentInteractionListener {


    private static final String TAG = MainActivity.class.getName(); //Log-Tag
    private static final int SESSION_NOT_FOUND_CODE = -1;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        //Set the second tab as start tab
        actionBar.setSelectedNavigationItem(1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onGameItemSelected(String id) {
        // TODO Open an activity displaying the  match with the given id
    }

    public void logoutUser(View view) {
        Log.d(TAG, "Logout clicked...");
        UserLogoutTask mTask = new UserLogoutTask(this, PrefUtils.getFromPrefs(getApplicationContext(), PrefUtils.PREFS_SESSION_ID_KEY, SESSION_NOT_FOUND_CODE));
        mTask.execute();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    // Top Rated fragment activity
                    return new ProfilFragment().newInstance(1);
                case 1:
                    // Games fragment activity
                    return new GamesFragment().newInstance(2);
                case 2:
                    // Movies fragment activity
                    return new HighscoreFragment().newInstance(3);
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    /**
     * A Highscore fragment containing a the game statistics
     *
     * @author Michael Landreh
     */
    public static class HighscoreFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HighscoreFragment newInstance(int sectionNumber) {
            HighscoreFragment fragment = new HighscoreFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public HighscoreFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_highscore, container, false);
            return rootView;
        }
    }

    /**
     * A profil fragment containing a profil overview
     *
     * @author Michael Landreh
     */
    public static class ProfilFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private TextView mNameView;
        private TextView mEmailView;
        private TextView mNotTheRightUserView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProfilFragment newInstance(int sectionNumber) {
            ProfilFragment fragment = new ProfilFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ProfilFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_profil, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            //Get Views of profilfragment
            mNameView = (TextView) getView().findViewById(R.id.nameView);
            mEmailView = (TextView) getView().findViewById(R.id.emailView);
            mNotTheRightUserView = (TextView) getView().findViewById(R.id.notTheRightUserView);

            //Get user info from shared preferences
            String firstname = PrefUtils.getFromPrefs(getActivity().getApplicationContext(), PrefUtils.PREFS_FIRSTNAME_KEY, "first name");
            String lastname = PrefUtils.getFromPrefs(getActivity().getApplicationContext(), PrefUtils.PREFS_LASTNAME_KEY, "last name");
            String email = PrefUtils.getFromPrefs(getActivity().getApplicationContext(), PrefUtils.PREFS_EMAIL_KEY, "e-mail");

            //Show user info in profil fragment
            mNotTheRightUserView.setText("Aren't you " + firstname + "?");
            mNameView.setText("Hello, " + firstname + " " + lastname + "!");
            mEmailView.setText(email);
        }
    }

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;
        private int mSessionId = -1;
        private Context context;

        UserLogoutTask(Activity thisActivity, int sessionId) {
            context = thisActivity.getApplicationContext();
            mSessionId = sessionId;
            dialog = new ProgressDialog(thisActivity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Log out...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OnlineServiceClient remote = new OnlineServiceClient();


            // Try to logout
            return remote.logoutUser(mSessionId);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            //Dismiss the ProgressDialog if it's showing
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //Delete user data and session id to SharedPreferences if logout was successful and navigat to start screen
            if (success) {
                SessionDataUtils.deleteSessionData(context);
                Toast.makeText(context, "You are logged out successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, SplashScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }


        }

        @Override
        protected void onCancelled() {
            //Dismiss the ProgressDialog if it's showing
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
