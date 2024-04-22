package pt.ulisboa.ist.pharmacist.session

import android.content.Context

/**
 * Session manager that uses shared preferences to store the session.
 *
 * @param context the application context
 *
 * @property accessToken the user's access token
 * @property username the user's username
 */
class SessionManagerSharedPrefs(private val context: Context) : SessionManager {

    private val prefs by lazy {
        context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
    }

    override val usedId: Long?
        get() = if (prefs.contains(USER_ID)) prefs.getLong(USER_ID, -1) else null

    override val accessToken: String?
        get() = prefs.getString(ACCESS_TOKEN, null)

    override val username: String?
        get() = prefs.getString(USERNAME, null)

    override fun setSession(
        userId: Long,
        accessToken: String,
        username: String
    ) {
        prefs.edit()
            .putString(ACCESS_TOKEN, accessToken)
            .putString(USERNAME, username)
            .putLong(USER_ID, userId)
            .apply()
    }

    override fun clearSession() {
        prefs.edit()
            .remove(ACCESS_TOKEN)
            .remove(USERNAME)
            .remove(USER_ID)
            .apply()
    }

    companion object {
        private const val SESSION_PREFS = "session"
        private const val ACCESS_TOKEN = "accessToken"
        private const val USERNAME = "username"
        private const val USER_ID = "userId"
    }
}
