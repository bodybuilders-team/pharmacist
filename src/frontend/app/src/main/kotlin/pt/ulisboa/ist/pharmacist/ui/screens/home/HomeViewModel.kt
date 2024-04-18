package pt.ulisboa.ist.pharmacist.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.addPharmacy.AddPharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeLoadingState.NOT_LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.launchAndExecuteRequest

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
    private var _loadingState by mutableStateOf(NOT_LOADING)
    private var _isLoggedIn: Boolean by mutableStateOf(sessionManager.isLoggedIn())

    val loadingState: HomeLoadingState
        get() = _loadingState

    val isLoggedIn
        get() = _isLoggedIn

    val username
        get() = sessionManager.username

    /**
     * Logs out the user.
     */
    fun logout() {
        check(sessionManager.isLoggedIn()) { "The user is not logged in." }

        val refreshToken = sessionManager.refreshToken
        sessionManager.clearSession()
        _isLoggedIn = false

        refreshToken ?: return

        launchAndExecuteRequest(
            request = { pharmacistService.usersService.logout(refreshToken) },
            events = _events,
            onSuccess = {},
            retryOnApiResultFailure = { false }
        )
    }

    fun navigateToPharmacyMap() {
        navigateTo(PharmacyMapActivity::class.java)
    }

    fun navigateToAddPharmacy() {
        navigateTo(AddPharmacyActivity::class.java)
    }

    fun navigateToSearchMedicine() {
        //TODO: navigateTo(SearchMedicineActivity::class.java)
    }

    /**
     * Navigates to the given activity.
     *
     * @param clazz the activity class to navigate to
     */
    fun <T> navigateTo(clazz: Class<T>) {
        _loadingState = LOADING

        viewModelScope.launch {
            _events.emit(HomeEvent.Navigate(clazz))
        }
    }

    /**
     * Navigates to the given activity.
     *
     * @param T the type of the activity to navigate to
     */
    inline fun <reified T> navigateTo() {
        navigateTo(T::class.java)
    }

    /**
     * Sets the loading state to [LOADED].
     */
    fun setLoadingStateToLoaded() {
        _loadingState = LOADED
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

    /**
     * The events of the [HomeViewModel].
     */
    sealed class HomeEvent : Event {

        /**
         * A navigation event.
         *
         * @property clazz the activity class to navigate to
         */
        class Navigate(val clazz: Class<*>) : HomeEvent()
    }
}
