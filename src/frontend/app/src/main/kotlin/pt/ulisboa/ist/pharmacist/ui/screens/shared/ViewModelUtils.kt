package pt.ulisboa.ist.pharmacist.ui.screens.shared

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Initializes a [ViewModel].
 *
 * @receiver the activity that will hold the view model
 * @param T the type of the [ViewModel] to be initialized
 * @param block the block of code to be executed to initialize the [ViewModel]
 *
 * @return the initialized [ViewModel]
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> ComponentActivity.viewModelInit(crossinline block: () -> T) =
    viewModels<T> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = block() as T
        }
    }
