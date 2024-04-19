package pt.ulisboa.ist.pharmacist.ui.screens

import android.app.Activity
import androidx.activity.ComponentActivity
import pt.ulisboa.ist.pharmacist.DependenciesContainer
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the [PharmacistScreen].
 * The base activity for all pharmacist activities.
 */
open class PharmacistActivity : ComponentActivity() {

    protected val dependenciesContainer by lazy {
        (application as DependenciesContainer)
    }

    /**
     * Gets an initialized [PharmacistViewModel].
     *
     * @param T the type of the [PharmacistViewModel] to be initialized
     * @param constructor the constructor for the view model
     *
     * @return the view model
     */
    protected inline fun <reified T : PharmacistViewModel> getViewModel(
        crossinline constructor: (
            pharmacistService: PharmacistService,
            sessionManager: SessionManager
        ) -> T
    ) = viewModelInit {
        constructor(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager
        )
    }
}
