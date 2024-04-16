package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.about.AboutActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.LoginActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.RegisterActivity
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeEvent
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeViewModel.HomeState.IDLE
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ToastDuration
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateToForResult
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the [HomeScreen].
 */
class HomeActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::HomeViewModel)

    private val userHomeForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultIntent = result.data ?: return@registerForActivityResult
        // This callback runs on the main thread
        viewModel.loadUserHome()
    }

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
            is HomeEvent.Navigate -> {
                navigateToForResult(
                    activityResultLauncher = userHomeForResult,
                    clazz = event.clazz
                )

                viewModel.setLoadingStateToLoaded()
            }
            is Event.Error -> showToast(event.message, ToastDuration.LONG)
        }
    }
}
