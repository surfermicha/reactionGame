package de.sive.reactiongame.session;

import android.content.Context;

import de.sive.reactiongame.PrefUtils;

/**
 * This class saves Session data to the SharedPreferences
 * @author Michael Landreh
 */
public class SessionDataUtils {

    public static void saveSessionData(Context context, int sessionId, String email, String firstname, String lastname) {

        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_SESSION_ID_KEY, sessionId);
        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_EMAIL_KEY, email);
        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_FIRSTNAME_KEY, firstname);
        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_LASTNAME_KEY, lastname);
    }
}
