package pt.ulisboa.ist.pharmacist.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeLoadingState.NOT_LOADING

/**
 * View model for the [HomeActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property loadingState the current loading state of the view model
 */
class HomeViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loadingState by mutableStateOf(NOT_LOADING)
        private set
    var isLoggedIn: Boolean by mutableStateOf(sessionManager.isLoggedIn())
        private set

    val username
        get() = sessionManager.username

    /**
     * Logs out the user.
     */
    fun logout() {
        check(sessionManager.isLoggedIn()) { "The user is not logged in." }

        sessionManager.clearSession()
        this.isLoggedIn = false
    }

    /**
     * Sets the loading state to [LOADED].
     */
    fun setLoadingStateToLoaded() {
        loadingState = LOADED
    }

    /**
     * The loading state of the [HomeViewModel].
     *
     * @property NOT_LOADING the home screen is idle
     * @property LOADING the home screen is loading
     * @property LOADED the home screen is not loading
     */
    enum class HomeLoadingState {
        NOT_LOADING,
        LOADING,
        LOADED
    }

}
