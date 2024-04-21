package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.LoginViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.home.HomeActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateTo
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the register screen.
 *
 * @property viewModel the view model used to handle the register process
 */
class RegisterActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::RegisterViewModel)

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
            LaunchedEffect(viewModel.registerState) {
                if (viewModel.registerState == RegisterViewModel.RegisterState.REGISTERED) {
                    finish()
                }
            }

            RegisterScreen(
                state = viewModel.registerState,
                onRegister = { email, username, password ->
                    viewModel.register(email = email, username = username, password = password)
                }
            )
        }
    }

}
