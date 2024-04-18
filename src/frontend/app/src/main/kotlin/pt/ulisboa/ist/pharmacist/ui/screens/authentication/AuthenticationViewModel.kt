package pt.ulisboa.ist.pharmacist.ui.screens.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.users.models.AuthenticationOutput
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel.AuthenticationState.IDLE
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel.AuthenticationState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel.AuthenticationState.SUCCESS
import pt.ulisboa.ist.pharmacist.ui.screens.shared.launchAndExecuteRequest

/**
 * View model for both authentication methods (login and register).
 *
 * @property sessionManager the manager used to handle the user session
 *
 * @property _state the current state of the view model
 * @property events the events that occurred in the view model
 */
open class AuthenticationViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {

    private var _state: AuthenticationState by mutableStateOf(IDLE)
    val state
        get() = _state

    /**
     * Executes an authentication request.
     *
     * @param username the username of the user
     * @param getAuthenticationResult the result of the authentication process
     */
    protected fun executeAuthenticationRequest(
        username: String,
        getAuthenticationResult: suspend () -> APIResult<AuthenticationOutput>
    ) {
        check(_state == IDLE) { "The view model is not in the idle state." }

        _state = LOADING

        launchAndExecuteRequest(
            request = { getAuthenticationResult() },
            events = _events,
            onSuccess = { authenticationData ->
                sessionManager.setSession(
                    accessToken = authenticationData.accessToken,
                    refreshToken = authenticationData.refreshToken,
                    username = username
                )
                _state = SUCCESS
            },
            retryOnApiResultFailure = {
                _state = IDLE
                false
            }
        )
    }

    /**
     * The state of an authentication process.
     *
     * @property IDLE the initial state
     * @property LOADING the state of the authentication process while it is loading
     * @property SUCCESS the state of the authentication process when it is successful
     */
    enum class AuthenticationState {
        IDLE,
        LOADING,
        SUCCESS
    }
}
