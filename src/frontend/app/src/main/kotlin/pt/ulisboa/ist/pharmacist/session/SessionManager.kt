package pt.ulisboa.ist.pharmacist.session

import kotlinx.coroutines.flow.SharedFlow

/**
 * Responsible for holding a user's session.
 *
 * @property userId the user's id
 * @property accessToken the user's access token
 * @property username the user's username
 * @property isGuest true if the user is a guest, false otherwise
 */
interface SessionManager {
    val userId: Long?
    val accessToken: String?
    val username: String?
    val isGuest: Boolean
    val logInFlow: SharedFlow<Boolean>

    /**
     * Checks if the user is logged in.
     *
     * @return true if the user is logged in, false otherwise
     */
    fun isLoggedIn(): Boolean = accessToken != null

    /**
     * Updates the session with the given tokens and username.
     *
     * @param userId the user's id
     * @param accessToken the user's access token
     * @param username the user's username
     * @param isGuest true if the user is a guest, false otherwise
     */
    fun setSession(
        userId: Long,
        accessToken: String,
        username: String,
        isGuest: Boolean = false
    )

    /**
     * Clears the session.
     *
     */
    fun clearSession()
}
