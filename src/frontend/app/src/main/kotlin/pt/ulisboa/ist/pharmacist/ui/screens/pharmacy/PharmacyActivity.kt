package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateTo
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the [PharmacyScreen].
 */
class PharmacyActivity : PharmacistActivity() {

    private val pharmacyId by lazy {
        intent.getLongExtra(PHARMACY_ID, -1)
    }

    private val viewModel by viewModelInit {
        PharmacyViewModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager,
            pharmacyId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.loadingState == PharmacyViewModel.PharmacyLoadingState.NOT_LOADED)
            viewModel.loadPharmacy(pharmacyId)

        setContent {
            PharmacyScreen(
                pharmacy = viewModel.pharmacy,
                loadingState = viewModel.loadingState,
                onNavigateToPharmacyClick = { location ->
                    val gmmIntentUri =
                        Uri.parse("geo:0,0?q=${location.lat},${location.lon}(${viewModel.pharmacy?.name})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    startActivity(mapIntent)
                },
                medicinesState = viewModel.medicinesState,
                onMedicineClick = { medicineId ->
                    MedicineActivity.navigate(this, medicineId)
                }
            )
        }
    }

    companion object {
        private const val PHARMACY_ID = "pharmacyId"

        /**
         * Navigates to the [PharmacyActivity].
         *
         * @param context the context from which to navigate
         * @param pharmacyId the id of the pharmacy to navigate to
         */
        fun navigate(context: Context, pharmacyId: Long) {
            context.navigateTo<PharmacyActivity> {
                it.putExtra(PHARMACY_ID, pharmacyId)
            }
        }
    }
}
