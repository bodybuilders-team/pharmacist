package pt.ulisboa.ist.pharmacist.ui.screens

import androidx.lifecycle.ViewModel
import pt.ulisboa.ist.pharmacist.session.SessionManager

/**
 * View model for the [PharmacistActivity].
 * Base class for all view models that are used in the application.
 *
 * @property sessionManager the manager used to handle the user session
 */
abstract class PharmacistViewModel(
    protected val sessionManager: SessionManager
) : ViewModel()