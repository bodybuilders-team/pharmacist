package pt.ulisboa.ist.pharmacist.ui.screens.authentication.upgrade

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.UpgradeScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.showToast

/**
 * Activity for the upgrade account screen.
 *
 * @property viewModel the view model used to handle the upgrade account screen
 */
class UpgradeActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::UpgradeViewModel)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is UpgradeViewModel.Event.ShowToast -> showToast(event.message)
                }
            }
        }

        setContent {
            LaunchedEffect(viewModel.upgradeState) {
                if (viewModel.upgradeState == UpgradeViewModel.UpgradeState.LOGGED_IN) {
                    finish()
                }
            }

            UpgradeScreen(
                state = viewModel.upgradeState,
                onUpgrade = { username, password ->
                    viewModel.upgradeAccount(username = username, password = password)
                },
            )
        }


    }

}
