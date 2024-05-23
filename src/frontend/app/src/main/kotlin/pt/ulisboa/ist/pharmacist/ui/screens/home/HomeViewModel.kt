package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import pt.ulisboa.ist.pharmacist.repository.network.connection.isSuccess
import pt.ulisboa.ist.pharmacist.repository.remote.users.UsersApi
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import java.util.UUID
import javax.inject.Inject

/**
 * View model for the [HomeActivity].
 *
 * @property sessionManager the manager used to handle the user session
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val usersApi: UsersApi,
    sessionManager: SessionManager
) : PharmacistViewModel(sessionManager) {

    var isLoggedIn: Boolean by mutableStateOf(sessionManager.isLoggedIn())
        private set

    var isGuest: Boolean by mutableStateOf(sessionManager.isGuest)
        private set

    val username
        get() = sessionManager.username

    var isLoading: Boolean by mutableStateOf(false)
        private set

    /**
     * Logs out the user.
     */
    fun logout() = viewModelScope.launch {
        if (!sessionManager.isLoggedIn()) {
            Log.d("LOGOUT", "User is not logged in")

            sessionManager.clearSession()
            isLoggedIn = false
            isGuest = false
            return@launch
        }

        Log.d("LOGOUT", "Logging out user")
        withTimeoutOrNull(5000) {
            isLoading = true
            try {
                usersApi.logout()
            } catch (e: Exception) {
                Log.e("LOGOUT", "Error logging out user", e)
            }
        }

        sessionManager.clearSession()
        isLoggedIn = false
        isGuest = false
        isLoading = false
        Log.d("LOGOUT", "User logged out")
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
        isLoading = true
        val result = try {
            usersApi.register(
                username = guestUserName,
                password = UUID.randomUUID().toString()
            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error entering as guest", e)
            isLoading = false
            return@launch
        }

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
        isLoading = false
    }
}
