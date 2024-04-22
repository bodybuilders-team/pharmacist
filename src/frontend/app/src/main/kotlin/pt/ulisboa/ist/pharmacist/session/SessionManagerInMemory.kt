package pt.ulisboa.ist.pharmacist.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Responsible for holding a user's session, in memory.
 */
@Suppress("unused")
class SessionManagerInMemory : SessionManager {
    private var _accessToken: String? by mutableStateOf(null)
    private var _username: String? by mutableStateOf(null)
    private var _userId: Long? by mutableStateOf(null)

    override val accessToken
        get() = _accessToken

    override val username
        get() = _username

    override val usedId: Long?
        get() = _userId

    override fun setSession(userId: Long, accessToken: String, username: String) {
        this._userId = userId
        this._accessToken = accessToken
        this._username = username
    }

    override fun clearSession() {
        this._accessToken = null
        this._username = null
        this._userId = null
    }
}
