package pt.ulisboa.ist.pharmacist.ui.screens.medicine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateTo
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the [MedicineScreen].
 */
class MedicineActivity : PharmacistActivity() {

    private val medicineId by lazy {
        intent.getLongExtra(MEDICINE_ID, -1)
    }

    private val viewModel by viewModelInit {
        MedicineViewModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager,
            medicineId
        )
    }

    companion object {
        const val MEDICINE_ID = "medicineId"

        /**
         * Navigates to the [MedicineActivity].
         *
         * @param context the context from which to navigate
         * @param medicineId the id of the medicine to navigate to
         */
        fun navigate(context: Context, medicineId: Long) {
            context.navigateTo<MedicineActivity> {
                putExtra(MEDICINE_ID, medicineId)
            }
        }

        fun getNavigationIntent(context: Context, medicineId: Long): Intent {
            return Intent(context, MedicineActivity::class.java).apply {
                putExtra(MEDICINE_ID, medicineId)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.loadingState == NOT_LOADED)
            viewModel.loadMedicine(medicineId)


        viewModel.checkForLocationAccessPermission(this)

        lifecycleScope.launch {
            viewModel.startObtainingLocation(this@MedicineActivity)
        }

        setContent {
            MedicineScreen(
                hasLocationPermission = viewModel.hasLocationPermission,
                medicineModel = viewModel.medicine,
                loadingState = viewModel.loadingState,
                pharmaciesState = viewModel.pharmaciesState,
                onPharmacyClick = { pharmacy ->
                    PharmacyActivity.navigate(this, pharmacy.pharmacyId)
                },
                toggleMedicineNotification = {
                    viewModel.toggleMedicineNotification()
                }
            )
        }
    }


}
