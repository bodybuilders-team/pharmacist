package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap

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
 * Activity for the [PharmacyMapScreen].
 */
class PharmacyMapActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::PharmacyMapViewModel)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.events.collect {
                handleEvent(it)
            }
        }

        setContent {
            PharmacyMapScreen(
                onPharmacyDetailsClick = { pid -> viewModel.navigateToPharmacyDetails(pid) },
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
            is PharmacyMapViewModel.PharmacyMapEvent.Navigate -> {
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
