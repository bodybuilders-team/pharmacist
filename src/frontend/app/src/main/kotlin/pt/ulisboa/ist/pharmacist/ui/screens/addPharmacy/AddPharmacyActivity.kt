package pt.ulisboa.ist.pharmacist.ui.screens.addPharmacy

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ToastDuration
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the [AddPharmacyScreen].
 */
class AddPharmacyActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::AddPharmacyViewModel)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.events.collect {
                handleEvent(it)
            }
        }

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
    @RequiresApi(Build.VERSION_CODES.R)
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
