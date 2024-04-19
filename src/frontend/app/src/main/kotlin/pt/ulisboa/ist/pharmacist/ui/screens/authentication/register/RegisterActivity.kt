package pt.ulisboa.ist.pharmacist.ui.screens.authentication.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.Event
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the register screen.
 *
 * @property viewModel the view model used to handle the register process
 */
class RegisterActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::RegisterViewModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            RegisterScreen(
                state = viewModel.state,
                onRegister = { email, username, password ->
                    viewModel.register(email = email, username = username, password = password)
                },
                onRegisterSuccessful = {
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
