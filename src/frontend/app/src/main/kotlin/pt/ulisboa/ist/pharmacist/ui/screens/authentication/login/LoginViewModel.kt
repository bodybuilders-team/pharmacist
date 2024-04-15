package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel

/**
 * View model for the [LoginActivity].
 *
 * @param sessionManager the manager used to handle the user session
 */
class LoginViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : AuthenticationViewModel(pharmacistService, sessionManager) {

    /**
     * Attempts to login the user with the given credentials.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    fun login(username: String, password: String) {
        executeAuthenticationRequest(username = username) {
            pharmacistService.usersService.login(
                username = username,
                password = password
            )
        }
    }
}
