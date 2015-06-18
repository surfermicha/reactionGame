package de.sive.reactiongame.session;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GameServer.SessionData;
import de.sive.reactiongame.R;
import de.sive.reactiongame.onlineClient.OnlineServiceClient;
import de.sive.reactiongame.onlineClient.UserAlreadyExistsException;


/**
 * A Registration screen that offers Registration via email/password/firstname/lastname.
 *
 * @author Michael Landreh
 */
public class RegistrationActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {


    private static final String TAG = RegistrationActivity.class.getName(); //Log-Tag
    private static final int REGISTRATION_USER_ALREADY_EXISTS_CODE = 10; //Error code if the user to create already exists
    private static final int REGISTRATION_ERROR_CODE = 80; // Error code for any other technical exception
    private static final int OK_CODE = 0; // OK code
    public static final String EXTRA_EMAIL = "de.sive.reactiongame.EMAIL"; //Intent extra key: e-mail
    public static final String EXTRA_PASSWORD = "de.sive.reactiongame.PASSWORD"; //Intent extra key: password

    /**
     * Keep track of the Registration task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmationView;
    private EditText mFirstnameView;
    private EditText mLastnameView;
    private View mProgressView;
    private View mRegistrationFormView;
    private SessionData sessionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Set up the Registration form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.Registration || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        Intent receivedIntent = getIntent();
        mEmailView.setText(receivedIntent.getStringExtra(LoginActivity.EXTRA_EMAIL));
        mPasswordView.setText(receivedIntent.getStringExtra(LoginActivity.EXTRA_PASSWORD));

        mRegistrationFormView = findViewById(R.id.Registration_form);
        mProgressView = findViewById(R.id.Registration_progress);
        mPasswordConfirmationView = (EditText) findViewById(R.id.password_confirmation);
        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mLastnameView = (EditText) findViewById(R.id.lastname);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the Registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual Registration attempt is made.
     */
    public void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the Registration attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString();
        String passwordConfirmation = mPasswordConfirmationView.getText().toString();
        String firstname = mFirstnameView.getText().toString().trim();
        String lastname = mLastnameView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check for valid password confirmation
        if (TextUtils.isEmpty(passwordConfirmation)) {
            mPasswordConfirmationView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmationView;
            cancel = true;
        } else if (!password.equals(passwordConfirmation)) {
            mPasswordConfirmationView.setError(getString(R.string.error_invalid_password_confirmation));
            focusView = mPasswordConfirmationView;
            cancel = true;
        }

        //Check for valid firstname
        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        }

        //Check for valid lastname
        if (TextUtils.isEmpty(lastname)) {
            mLastnameView.setError(getString(R.string.error_field_required));
            focusView = mLastnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt Registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user Registration attempt.
            showProgress(true);
            mAuthTask = new UserRegistrationTask(this, email, password, firstname, lastname);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    private boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param password Password
     * @return boolean true for valid false for invalid
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the Registration form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public void onSignInButtonPressed(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(EXTRA_EMAIL, mEmailView.getText().toString());
        intent.putExtra(EXTRA_PASSWORD, mPasswordView.getText().toString());
        startActivity(intent);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstname;
        private final String mLastname;
        private Context context;

        UserRegistrationTask(Activity thisActivity, String email, String password, String firstname, String lastname) {
            context = thisActivity.getApplicationContext();
            mEmail = email;
            mPassword = password;
            mFirstname = firstname;
            mLastname = lastname;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            OnlineServiceClient remote = new OnlineServiceClient();
            Integer statusCode = Integer.valueOf(OK_CODE);
            try {
                sessionData = remote.createUser(mEmail, mPassword, mFirstname, mLastname);
                //Safe credentials in SharedPreferences
                SessionDataUtils.saveSessionData(context, sessionData.getSessionId(), sessionData.getEmail(), sessionData.getFirstname(), sessionData.getLastname());
            } catch (UserAlreadyExistsException e) {
                Log.w(TAG, e.getMessage());
                statusCode = Integer.valueOf(REGISTRATION_USER_ALREADY_EXISTS_CODE);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                statusCode = Integer.valueOf(REGISTRATION_ERROR_CODE);
            }


            return statusCode;
        }

        @Override
        protected void onPostExecute(final Integer statusCode) {
            mAuthTask = null;
            showProgress(false);

            switch (statusCode) {
                case OK_CODE:
                    Log.d(TAG, "Registration was successful!");
                    Toast.makeText(context, "Registration was successful! You are now logged in.", Toast.LENGTH_LONG).show();
                    //TODO: Redirect to main activity
                    break;
                case REGISTRATION_USER_ALREADY_EXISTS_CODE:
                    mEmailView.setError("A user with this email address already exists");
                    mEmailView.requestFocus();
                    break;
                case REGISTRATION_ERROR_CODE:
                    Toast.makeText(context, "An technical Error occurs. Please contact us!", Toast.LENGTH_LONG).show();
                    mEmailView.requestFocus();
                    break;
                default:
                    Toast.makeText(context, "An unknown Error occurs. Please contact us!", Toast.LENGTH_LONG).show();
                    mEmailView.requestFocus();
                    break;
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
            Toast.makeText(context, "An unknown Error occurs. Please contact us!", Toast.LENGTH_LONG).show();
        }
    }
}

