package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.about.AboutActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.LoginActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.RegisterActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ToastDuration
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateTo
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the [HomeScreen].
 */
class HomeActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::HomeViewModel)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.events.collect {
                handleEvent(it)
            }
        }

        setContent {
            HomeScreen(
                loggedIn = viewModel.isLoggedIn,
                username = viewModel.username,
                onLoginClick = { viewModel.navigateTo<LoginActivity>() },
                onRegisterClick = { viewModel.navigateTo<RegisterActivity>() },
                onLogoutClick = { viewModel.logout() },
                onAboutClick = { viewModel.navigateTo<AboutActivity>() },
                onPharmacyMapClick = { viewModel.navigateToPharmacyMap() },
                onAddPharmacyClick = { viewModel.navigateToAddPharmacy() },
                onSearchMedicineClick = { viewModel.navigateToSearchMedicine() },
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
            is HomeViewModel.HomeEvent.Navigate -> {
                navigateTo(event.clazz)
                viewModel.setLoadingStateToLoaded()
            }

            is Event.Error -> showToast(event.message, ToastDuration.LONG)
        }
    }
}
