package pt.ulisboa.ist.pharmacist.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event

/**
 * View model for the [PharmacistActivity].
 * Base class for all view models that are used in the application.
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 * @property events the events that occurred in the view model
 */
abstract class PharmacistViewModel(
    private val pharmacistService: PharmacistService,
    protected val sessionManager: SessionManager
) : ViewModel() {

    @Suppress("PropertyName")
    protected val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events
}
