package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.createMedicine.CreateMedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateToForResult
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the [MedicineScreen].
 */
class AddMedicineToPharmacyActivity : PharmacistActivity() {

    private val pharmacyId by lazy {
        intent.getLongExtra(PHARMACY_ID, -1)
    }

    private val viewModel by viewModelInit {
        AddMedicineToPharmacyViewModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager,
            pharmacyId
        )
    }

    private val createMedicineResultLauncher =
        CreateMedicineActivity.registerForResult(this) { medicineId ->
            if (medicineId != null)
                viewModel.addMedicine(medicineId)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkForLocationAccessPermission(this)

        lifecycleScope.launch {
            viewModel.startObtainingLocation(this@AddMedicineToPharmacyActivity)
        }

        setContent {
            AddMedicineToPharmacyScreen(
                hasLocationPermission = viewModel.hasLocationPermission,
                medicinesState = viewModel.medicinesState,
                onSearch = { viewModel.searchMedicines(it) },
                onMedicineClicked = { medicine ->
                    viewModel.selectedMedicine = medicine
                },
                selectedMedicine = viewModel.selectedMedicine,
                createMedicine = {
                    CreateMedicineActivity.navigateForResult(this, createMedicineResultLauncher)
                },
                addMedicineToPharmacy = { medicineId, stock ->
                    viewModel.addMedicineToPharmacy(medicineId, stock)
                }
            )
        }
    }

    companion object {
        private const val PHARMACY_ID = "pharmacyId"
        private const val MEDICINE_ID = "medicineId"

        fun navigateForResult(
            context: Context,
            resultLauncher: ActivityResultLauncher<Intent>,
            pharmacyId: Long
        ) {
            context.navigateToForResult<AddMedicineToPharmacyActivity>(resultLauncher) {
                it.putExtra(PHARMACY_ID, pharmacyId)
            }
        }

        fun registerForResult(activity: ComponentActivity, callback: (Long?) -> Unit) =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) callback(null)

                val resultIntent = result.data ?: return@registerForActivityResult
                val medicineId = resultIntent.getLongExtra(MEDICINE_ID, -1)

                callback(medicineId)
            }
    }

}