package pt.ulisboa.ist.pharmacist.ui.screens.authentication

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
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationActivity.Companion.AuthenticationMethod

/**
 * View model for the [AuthenticationActivity].
 *
 * @param sessionManager the manager used to handle the user session
 * @param authenticationMethod the authentication method
 * @param pharmacistService the service used to interact with the pharmacist API
 */
class AuthenticationViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager,
    private val authenticationMethod: AuthenticationMethod
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var authenticationState: AuthenticationState by mutableStateOf(AuthenticationState.NOT_AUTHENTICATED)
        private set

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events

    /**
     * Authenticates the user.
     *
     * @param username the username
     * @param password the password
     */
    fun authenticate(username: String, password: String) = viewModelScope.launch {
        authenticationState = AuthenticationState.AUTHENTICATING

        when (authenticationMethod) {
            AuthenticationMethod.REGISTER -> register(username, password)
            AuthenticationMethod.LOGIN -> login(username, password)
            AuthenticationMethod.UPGRADE -> upgrade(username, password)
        }
    }

    private fun register(username: String, password: String) = viewModelScope.launch {
        val result = pharmacistService.usersService.register(
            username = username,
            password = password
        )

        authenticationState = if (result.isSuccess()) {
            sessionManager.setSession(
                result.data.userId,
                result.data.accessToken,
                username
            )
            AuthenticationState.AUTHENTICATED
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            AuthenticationState.NOT_AUTHENTICATED
        }
    }

    private fun login(username: String, password: String) = viewModelScope.launch {
        val result = pharmacistService.usersService.login(
            username = username,
            password = password
        )

        authenticationState = if (result.isSuccess()) {
            sessionManager.setSession(
                result.data.userId,
                result.data.accessToken,
                username
            )
            AuthenticationState.AUTHENTICATED
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            AuthenticationState.NOT_AUTHENTICATED
        }
    }

    private fun upgrade(username: String, password: String) = viewModelScope.launch {
        val result = pharmacistService.usersService.upgrade(
            username = username,
            password = password
        )

        authenticationState = if (result.isSuccess()) {
            sessionManager.setSession(
                sessionManager.usedId!!,
                sessionManager.accessToken!!,
                username
            )
            AuthenticationState.AUTHENTICATED
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            AuthenticationState.NOT_AUTHENTICATED
        }
    }

    enum class AuthenticationState {
        NOT_AUTHENTICATED,
        AUTHENTICATING,
        AUTHENTICATED
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
    }
}
