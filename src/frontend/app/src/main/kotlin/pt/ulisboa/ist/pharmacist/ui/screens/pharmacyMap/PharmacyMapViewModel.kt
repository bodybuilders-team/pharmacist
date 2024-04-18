package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event

/**
 * View model for the [PharmacyMapActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property loadingState the current loading state of the view model
 * @property state the current state of the view model
 */
class PharmacyMapViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {

    private var _loadingState by mutableStateOf(PharmacyMapLoadingState.NOT_LOADING)
    private var _state: PharmacyState by mutableStateOf(PharmacyState.IDLE)

    val loadingState: PharmacyMapLoadingState
        get() = _loadingState

    val state
        get() = _state

    /**
     * Loads the pharmacy home page.
     */
    fun loadPharmacyMap() {
        check(state == PharmacyState.IDLE) { "The view model is not in the idle state." }

        _state = PharmacyState.LOADING_PHARMACY_MAP
        _state = PharmacyState.PHARMACY_MAP_LOADED
    }

    fun navigateToPharmacyDetails(pharmacyId: String) {
        /*TODO: _loadingState = LOADING

        viewModelScope.launch {
            while (state !in listOf(PharmacyState.PHARMACY_MAP_LOADED))
                yield()

            _events.emit(PharmacyMapEvent.Navigate(PharmacyActivity::class.java))
        }*/
    }

    /**
     * Navigates to the given activity.
     *
     * @param clazz the activity class to navigate to
     */
    fun <T> navigateTo(clazz: Class<T>) {
        _loadingState = PharmacyMapLoadingState.LOADING

        viewModelScope.launch {
            while (state !in listOf(PharmacyState.PHARMACY_MAP_LOADED))
                yield()

            _events.emit(PharmacyMapEvent.Navigate(clazz))
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
        _loadingState = PharmacyMapLoadingState.LOADED
    }


    enum class PharmacyState {
        IDLE,
        LOADING_PHARMACY_MAP,
        PHARMACY_MAP_LOADED
    }

    /**
     * The loading state of the [PharmacyMapViewModel].
     *
     * @property NOT_LOADING the home screen is idle
     * @property LOADING the home screen is loading
     * @property LOADED the home screen is not loading
     */
    enum class PharmacyMapLoadingState {
        NOT_LOADING,
        LOADING,
        LOADED
    }

    /**
     * The events of the [PharmacyMapViewModel].
     */
    sealed class PharmacyMapEvent : Event {

        /**
         * A navigation event.
         *
         * @property clazz the activity class to navigate to
         */
        class Navigate(val clazz: Class<*>) : PharmacyMapEvent()
    }
}
