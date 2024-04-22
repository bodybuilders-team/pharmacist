package pt.ulisboa.ist.pharmacist.session

// TODO: Maybe change depending on the implementation of the authentication mechanism in the backend

/**
 * Responsible for holding a user's session.
 *
 * @property usedId the user's id
 * @property accessToken the user's access token
 * @property username the user's username
 */
interface SessionManager {
    val usedId: Long?
    val accessToken: String?
    val username: String?

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
     */
    fun setSession(
        userId: Long,
        accessToken: String,
        username: String
    )

    /**
     * Clears the session.
     */
    fun clearSession()
}
