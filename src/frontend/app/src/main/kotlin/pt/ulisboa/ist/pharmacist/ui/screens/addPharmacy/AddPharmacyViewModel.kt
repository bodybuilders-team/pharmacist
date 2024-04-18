package pt.ulisboa.ist.pharmacist.ui.screens.addPharmacy

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
 * View model for the [AddPharmacyActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property loadingState the current loading state of the view model
 * @property state the current state of the view model
 */
class AddPharmacyViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {

    private var _loadingState by mutableStateOf(AddPharmacyLoadingState.NOT_LOADING)
    private var _state: AddPharmacyState by mutableStateOf(AddPharmacyState.IDLE)

    val loadingState: AddPharmacyLoadingState
        get() = _loadingState

    val state
        get() = _state

    /**
     * Loads the pharmacy home page.
     */
    fun loadPharmacyMap() {
        check(state == AddPharmacyState.IDLE) { "The view model is not in the idle state." }

        _state = AddPharmacyState.LOADING_ADD_PHARMACY
        _state = AddPharmacyState.ADD_PHARMACY_LOADED
    }

    /**
     * Navigates to the given activity.
     *
     * @param clazz the activity class to navigate to
     */
    fun <T> navigateTo(clazz: Class<T>) {
        _loadingState = AddPharmacyLoadingState.LOADING

        viewModelScope.launch {
            while (state !in listOf(AddPharmacyState.ADD_PHARMACY_LOADED))
                yield()

            _events.emit(AddPharmacyEvent.Navigate(clazz))
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
        _loadingState = AddPharmacyLoadingState.LOADED
    }


    enum class AddPharmacyState {
        IDLE,
        LOADING_ADD_PHARMACY,
        ADD_PHARMACY_LOADED
    }

    /**
     * The loading state of the [AddPharmacyViewModel].
     *
     * @property NOT_LOADING the home screen is idle
     * @property LOADING the home screen is loading
     * @property LOADED the home screen is not loading
     */
    enum class AddPharmacyLoadingState {
        NOT_LOADING,
        LOADING,
        LOADED
    }

    /**
     * The events of the [AddPharmacyViewModel].
     */
    sealed class AddPharmacyEvent : Event {

        /**
         * A navigation event.
         *
         * @property clazz the activity class to navigate to
         */
        class Navigate(val clazz: Class<*>) : AddPharmacyEvent()
    }
}
