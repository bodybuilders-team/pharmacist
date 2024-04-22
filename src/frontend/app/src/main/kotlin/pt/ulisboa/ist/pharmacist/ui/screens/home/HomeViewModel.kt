package pt.ulisboa.ist.pharmacist.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.LoginViewModel
import java.util.UUID

/**
 * View model for the [HomeActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 */
class HomeViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {

    var isLoggedIn: Boolean by mutableStateOf(sessionManager.isLoggedIn())
        private set

    val username
        get() = sessionManager.username

    /**
     * Logs out the user.
     */
    fun logout() = viewModelScope.launch {
        check(sessionManager.isLoggedIn()) { "The user is not logged in." }

        pharmacistService.usersService.logout()

        sessionManager.clearSession()
        isLoggedIn = false
    }

    fun checkIfLoggedIn() {
        isLoggedIn = sessionManager.isLoggedIn()
    }

    fun enterAsGuest() = viewModelScope.launch {
        val guestUserName = "Guest${UUID.randomUUID()}"
        val result = pharmacistService.usersService.register(
            username = guestUserName,
            password = UUID.randomUUID().toString()
        )

        if (result.isSuccess()) {
            sessionManager.setSession(result.data.userId, result.data.accessToken, guestUserName)
            isLoggedIn = true
        }
    }
}
