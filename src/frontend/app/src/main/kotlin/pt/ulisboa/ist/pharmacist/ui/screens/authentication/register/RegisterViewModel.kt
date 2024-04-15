package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register

import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel

/**
 * View model for the [RegisterActivity].
 *
 * @param sessionManager the manager used to handle the user session
 */
class RegisterViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : AuthenticationViewModel(pharmacistService, sessionManager) {

    /**
     * Attempts to register the user with the given credentials.
     *
     * @param email the email of the user
     * @param username the username of the user
     * @param password the password of the user
     */
    fun register(email: String, username: String, password: String) {
        executeAuthenticationRequest(username = username) {
            pharmacistService.usersService.register(
                email = email,
                username = username,
                password = password
            )
        }
    }
}
