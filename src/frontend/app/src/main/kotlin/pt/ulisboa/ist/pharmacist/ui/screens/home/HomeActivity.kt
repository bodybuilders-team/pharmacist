package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.about.AboutActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchActivity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateTo

/**
 * Activity for the [HomeScreen].
 */
@AndroidEntryPoint
class HomeActivity : PharmacistActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _: Boolean ->
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)

        setContent {
            HomeScreen(
                isLoading = viewModel.isLoading,
                loggedIn = viewModel.isLoggedIn,
                isGuest = viewModel.isGuest,
                username = viewModel.username,
                onLoginClick = {
                    AuthenticationActivity.navigate(
                        this,
                        AuthenticationActivity.Companion.AuthenticationMethod.LOGIN
                    )
                },
                onRegisterClick = {
                    AuthenticationActivity.navigate(
                        this,
                        AuthenticationActivity.Companion.AuthenticationMethod.REGISTER
                    )
                },
                onContinueAsGuestClick = { viewModel.enterAsGuest() },
                onUpgradeAccountClick = {
                    AuthenticationActivity.navigate(
                        this,
                        AuthenticationActivity.Companion.AuthenticationMethod.UPGRADE
                    )
                },
                onLogoutClick = { viewModel.logout() },
                onAboutClick = { navigateTo<AboutActivity>() },
                onPharmacyMapClick = { navigateTo<PharmacyMapActivity>() },
                onSearchMedicineClick = { navigateTo<MedicineSearchActivity>() }
            )
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.checkIfLoggedIn()
        viewModel.checkIfIsGuest()
    }
}
