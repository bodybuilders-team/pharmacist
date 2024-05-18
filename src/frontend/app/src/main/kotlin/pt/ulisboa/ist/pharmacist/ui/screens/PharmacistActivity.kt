package pt.ulisboa.ist.pharmacist.ui.screens

import androidx.activity.ComponentActivity
import pt.ulisboa.ist.pharmacist.DependenciesContainer

/**
 * Activity for the [PharmacistScreen].
 * The base activity for all pharmacist activities.
 */
open class PharmacistActivity : ComponentActivity() {

    protected val dependenciesContainer by lazy {
        (application as DependenciesContainer)
    }
}
