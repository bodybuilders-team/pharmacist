package pt.ulisboa.ist.pharmacist.ui.screens.home

import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.about.AboutActivity
import pt.ulisboa.ist.pharmacist.ui.screens.addPharmacy.AddPharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.login.LoginActivity
import pt.ulisboa.ist.pharmacist.ui.screens.authentication.register.RegisterActivity
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
                username = viewModel.username,
                onLoginClick = {
                    navigateTo<LoginActivity>()
                },
                onRegisterClick = {
                    navigateTo<RegisterActivity>()
                },
                onLogoutClick = {
                    viewModel.logout()
                },
                onAboutClick = {
                    navigateTo<AboutActivity>()
                },
                onPharmacyMapClick = {
                    navigateTo<PharmacyMapActivity>()
                },
                onAddPharmacyClick = {
                    navigateTo<AddPharmacyActivity>()
                },
                onSearchMedicineClick = {
                    navigateTo<MedicineSearchActivity>()
                },
                loadingState = viewModel.loadingState
            )
        }
    }


}
