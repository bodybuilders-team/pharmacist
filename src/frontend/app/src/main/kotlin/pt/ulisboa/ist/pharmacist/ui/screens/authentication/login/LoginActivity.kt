package pt.ulisboa.ist.pharmacist.ui.screens.authentication.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the login screen.
 *
 * @property viewModel the view model used to handle the login process
 */
class LoginActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::LoginViewModel)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    private suspend fun handleEvent(event: Event) {
        when (event) {
            is Event.Error -> showToast(event.message)
        }
    }
}
