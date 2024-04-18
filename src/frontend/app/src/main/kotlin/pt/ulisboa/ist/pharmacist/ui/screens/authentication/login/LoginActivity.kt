package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationViewModel.AuthenticationState.IDLE
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the login screen.
 *
 * @property viewModel the view model used to handle the login process
 */
class LoginActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::LoginViewModel)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.events.collect {
                handleEvent(it)
            }
        }

        setContent {
            LoginScreen(
                state = viewModel.state,
                onLogin = { username, password ->
                    viewModel.login(username = username, password = password)
                },
                onLoginSuccessful = { // TODO: maybe this is not necessary
                    val resultIntent = Intent()
                    setResult(RESULT_OK, resultIntent)
                    finish()
                },
                onBackButtonClicked = {
                    setResult(RESULT_CANCELED, null)
                    finish()
                }
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
            is Event.Error -> showToast(event.message)
        }
    }
}
