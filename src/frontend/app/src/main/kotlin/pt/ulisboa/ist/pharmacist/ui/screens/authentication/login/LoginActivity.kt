package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade.UpgradeViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

// TODO: Login, Register and Upgrade screens are essentially the same, with the same components and logic. They should be refactored to share the same components and logic:
// named AuthenticationScreen, AuthenticationViewModel, AuthenticationState, AuthenticationEvent, AuthenticationTextField, AuthenticationButton, etc.

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
            viewModel.events.collect { event ->
                when (event) {
                    is LoginViewModel.Event.ShowToast -> showToast(event.message)
                }
            }
        }

        setContent {
            LaunchedEffect(viewModel.loginState) {
                if (viewModel.loginState == LoginViewModel.LoginState.LOGGED_IN) {
                    finish()
                }
            }

            LoginScreen(
                state = viewModel.loginState,
                onLogin = { username, password ->
                    viewModel.login(username = username, password = password)
                },
            )
        }


    }

}
