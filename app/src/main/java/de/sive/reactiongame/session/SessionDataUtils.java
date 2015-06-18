package de.sive.reactiongame.session;

import android.content.Context;

import de.sive.reactiongame.PrefUtils;

/**
 * Created by Michael on 13.06.2015.
 */
public class SessionDataUtils {

    public static void saveSessionData(Context context, int sessionId, String email, String firstname, String lastname) {

        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_SESSION_ID_KEY, sessionId);
        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_EMAIL_KEY, email);
        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_FIRSTNAME_KEY, firstname);
        PrefUtils.saveToPrefs(context, PrefUtils.PREFS_LASTNAME_KEY, lastname);
    }
}
