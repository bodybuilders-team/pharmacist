package pt.ulisboa.ist.pharmacist.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Responsible for holding a user's session, in memory.
 */
@Suppress("unused")
class SessionManagerInMemory : SessionManager {
    private var _accessToken: String? by mutableStateOf(null)
    private var _username: String? by mutableStateOf(null)
    private var _userId: Long? by mutableStateOf(null)
    private var _isGuest: Boolean by mutableStateOf(false)
    private var _isSuspended: Boolean by mutableStateOf(false)

    override val accessToken
        get() = _accessToken

    override val username
        get() = _username

    override val userId: Long?
        get() = _userId

    override val isGuest: Boolean
        get() = _isGuest

    override val isSuspended: Boolean
        get() = _isSuspended

    override val logInFlow: MutableSharedFlow<Boolean> = MutableSharedFlow(extraBufferCapacity = 2)

    override fun setSession(
        userId: Long,
        accessToken: String,
        username: String,
        isGuest: Boolean,
        isSuspended: Boolean
    ) {
        this._userId = userId
        this._accessToken = accessToken
        this._username = username
        this._isGuest = isGuest
        this._isSuspended = isSuspended
    }

    override fun clearSession() {
        this._accessToken = null
        this._username = null
        this._userId = null
        this._isGuest = false
        this._isSuspended = false
    }
}
