package pt.ulisboa.ist.pharmacist.ui.screens.authentication

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateTo
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the authentication screen.
 *
 * @property viewModel the view model used to handle the authentication logic
 */
class AuthenticationActivity : PharmacistActivity() {

    private val authenticationMethod by lazy {
        AuthenticationMethod.valueOf(intent.getStringExtra(AUTHENTICATION_METHOD)!!)
    }

    private val viewModel by viewModelInit {
        AuthenticationViewModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager,
            authenticationMethod
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is AuthenticationViewModel.Event.ShowToast -> showToast(event.message)
                }
            }
        }

        setContent {
            LaunchedEffect(viewModel.authenticationState) {
                if (viewModel.authenticationState == AuthenticationViewModel.AuthenticationState.AUTHENTICATED) {
                    finish()
                }
            }

            AuthenticationScreen(
                state = viewModel.authenticationState,
                authenticationMethod = authenticationMethod,
                onAuthenticate = { username, password ->
                    viewModel.authenticate(username = username, password = password)
                }
            )
        }
    }

    companion object {
        private const val AUTHENTICATION_METHOD = "AUTHENTICATION_METHOD"

        /**
         * Navigates to the [AuthenticationActivity].
         *
         * @param context the context from which to navigate
         * @param authenticationMethod the authentication method
         */
        fun navigate(context: Context, authenticationMethod: AuthenticationMethod) {
            context.navigateTo<AuthenticationActivity> {
                putExtra(AUTHENTICATION_METHOD, authenticationMethod.toString())
            }
        }

        enum class AuthenticationMethod {
            REGISTER,
            LOGIN,
            UPGRADE
        }
    }

}
