package pt.ulisboa.ist.pharmacist.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel

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

    var isGuest: Boolean by mutableStateOf(sessionManager.isGuest)
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
        isGuest = false
    }

    /**
     * Checks if the user is logged in.
     */
    fun checkIfLoggedIn() {
        isLoggedIn = sessionManager.isLoggedIn()
    }

    /**
     * Checks if the user is a guest.
     */
    fun checkIfIsGuest() {
        isGuest = sessionManager.isGuest
    }

    /**
     * Enters the game as a guest.
     */
    fun enterAsGuest() = viewModelScope.launch {
        if (isLoggedIn || isGuest) return@launch

        val guestUserName = "Guest${UUID.randomUUID()}"
        val result = pharmacistService.usersService.register(
            username = guestUserName,
            password = UUID.randomUUID().toString()
        )

        if (result.isSuccess()) {
            sessionManager.setSession(
                result.data.userId,
                result.data.accessToken,
                guestUserName,
                isGuest = true
            )
            isLoggedIn = true
            isGuest = true
        }
    }
}
