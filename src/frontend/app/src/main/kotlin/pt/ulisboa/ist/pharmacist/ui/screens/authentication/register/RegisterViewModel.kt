package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register

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
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.LoginViewModel

/**
 * View model for the [RegisterActivity].
 *
 * @param sessionManager the manager used to handle the user session
 */
class RegisterViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var registerState by mutableStateOf(RegisterState.NOT_REGISTERED)
        private set

    private val _events = MutableSharedFlow<LoginViewModel.Event>()
    val events: SharedFlow<LoginViewModel.Event> = _events

    /**
     * Attempts to register the user with the given credentials.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    fun register(username: String, password: String) = viewModelScope.launch {
        registerState = RegisterState.REGISTERING

        val result = pharmacistService.usersService.register(
            username = username,
            password = password
        )

        registerState = if (result.isSuccess()) {
            sessionManager.setSession(result.data.userId, result.data.accessToken, username)
            RegisterState.REGISTERED
        } else {
            _events.emit(LoginViewModel.Event.ShowToast(result.error.title))
            RegisterState.NOT_REGISTERED
        }
    }

    enum class RegisterState {
        NOT_REGISTERED,
        REGISTERING,
        REGISTERED
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
    }
}
