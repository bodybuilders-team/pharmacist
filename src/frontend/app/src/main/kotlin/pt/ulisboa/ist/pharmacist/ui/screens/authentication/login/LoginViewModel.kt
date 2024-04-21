package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

import android.app.usage.UsageEvents.Event
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel

/**
 * View model for the [LoginActivity].
 *
 * @param sessionManager the manager used to handle the user session
 */
class LoginViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loginState: LoginState by mutableStateOf(
        LoginState.NOT_LOGGED_IN
    )
        private set

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events

    /**
     * Attempts to login the user with the given credentials.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    fun login(username: String, password: String) = viewModelScope.launch {
        loginState = LoginState.LOGGING_IN

        val result = pharmacistService.usersService.login(
            username = username,
            password = password
        )

        loginState = if (result.isSuccess()) {
            sessionManager.setSession(result.data.userId, result.data.accessToken, username)
            LoginState.LOGGED_IN
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            LoginState.NOT_LOGGED_IN
        }
    }

    enum class LoginState {
        NOT_LOGGED_IN,
        LOGGING_IN,
        LOGGED_IN
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
    }
}
