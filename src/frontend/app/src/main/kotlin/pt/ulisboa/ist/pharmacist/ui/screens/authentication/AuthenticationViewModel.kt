package pt.ulisboa.ist.pharmacist.ui.screens.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.users.UsersApi
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationActivity.Companion.AuthenticationMethod

/**
 * View model for the [AuthenticationActivity].
 *
 * @param sessionManager the manager used to handle the user session
 * @param authenticationMethod the authentication method
 */
@HiltViewModel
class AuthenticationViewModel @AssistedInject constructor(
    private val usersApi: UsersApi,
    sessionManager: SessionManager,
    @Assisted val authenticationMethod: AuthenticationMethod
) : PharmacistViewModel(sessionManager) {

    @AssistedFactory
    interface Factory {
        fun create(authenticationMethod: AuthenticationMethod): AuthenticationViewModel
    }

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
        val result = usersApi.register(
            username = username,
            password = password
        )

        authenticationState = if (result.isSuccess()) {
            sessionManager.setSession(
                result.data.userId,
                result.data.accessToken,
                username,
                isSuspended = result.data.isSuspended
            )
            AuthenticationState.AUTHENTICATED
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            AuthenticationState.NOT_AUTHENTICATED
        }
    }

    private fun login(username: String, password: String) = viewModelScope.launch {
        val result = usersApi.login(
            username = username,
            password = password
        )

        authenticationState = if (result.isSuccess()) {
            sessionManager.setSession(
                result.data.userId,
                result.data.accessToken,
                username,
                isSuspended = result.data.isSuspended
            )
            AuthenticationState.AUTHENTICATED
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            AuthenticationState.NOT_AUTHENTICATED
        }
    }

    private fun upgrade(username: String, password: String) = viewModelScope.launch {
        val result = usersApi.upgrade(
            username = username,
            password = password
        )

        authenticationState = if (result.isSuccess()) {
            sessionManager.setSession(
                sessionManager.userId!!,
                sessionManager.accessToken!!,
                username
            )
            AuthenticationState.AUTHENTICATED
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            AuthenticationState.NOT_AUTHENTICATED
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            authenticationMethod: AuthenticationMethod
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(authenticationMethod) as T
            }
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
