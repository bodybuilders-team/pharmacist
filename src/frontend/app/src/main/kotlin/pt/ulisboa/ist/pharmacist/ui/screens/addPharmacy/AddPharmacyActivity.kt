package pt.ulisboa.ist.pharmacist.ui.screens.addPharmacy

import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ToastDuration
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the [AddPharmacyScreen].
 */
class AddPharmacyActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::AddPharmacyViewModel)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddPharmacyScreen(
                loadingState = viewModel.loadingState
            )
        }
    }

    /**
     * Handles the specified event.
     *
     * @param event the event to handle
     */

    private suspend fun handleEvent(event: Event) {
        when (event) {
            is AddPharmacyViewModel.AddPharmacyEvent.Navigate -> {
                /*TODO: navigateToForResult(
                    activityResultLauncher = userHomeForResult,
                    clazz = event.clazz
                )

                viewModel.setLoadingStateToLoaded()*/
            }

            is Event.Error -> showToast(event.message, ToastDuration.LONG)
        }
    }
}
