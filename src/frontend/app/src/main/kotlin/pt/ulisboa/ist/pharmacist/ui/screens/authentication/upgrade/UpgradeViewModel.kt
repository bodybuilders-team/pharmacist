package pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel

/**
 * View model for the [UpgradeActivity].
 *
 * @param sessionManager the manager used to handle the user session
 */
class UpgradeViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var upgradeState: UpgradeState by mutableStateOf(
        UpgradeState.NOT_LOGGED_IN
    )
        private set

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events

    /**
     * Upgrades the user account.
     */
    fun upgradeAccount(username: String, password: String) = viewModelScope.launch {
        upgradeState = UpgradeState.LOGGING_IN

        val result = pharmacistService.usersService.upgrade(
            username = username,
            password = password
        )

        upgradeState = if (result.isSuccess()) {
            sessionManager.setSession(
                sessionManager.usedId!!,
                sessionManager.accessToken!!,
                username,
                isGuest = false
            )
            UpgradeState.LOGGED_IN
        } else {
            _events.emit(Event.ShowToast(result.error.title))
            UpgradeState.NOT_LOGGED_IN
        }
    }

    enum class UpgradeState {
        NOT_LOGGED_IN,
        LOGGING_IN,
        LOGGED_IN
    }

    sealed class Event {
        class ShowToast(val message: String) : Event()
    }
}
