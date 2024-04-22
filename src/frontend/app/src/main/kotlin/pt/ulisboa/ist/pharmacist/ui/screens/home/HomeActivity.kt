package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.about.AboutActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.AuthenticationActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchActivity
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateTo

/**
 * Activity for the [HomeScreen].
 */
class HomeActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::HomeViewModel)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeScreen(
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
