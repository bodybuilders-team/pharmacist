package pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyViewModel.AddMedicineToPharmacyState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.createMedicine.CreateMedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateToForResult
import javax.inject.Inject

/**
 * Activity for the [AddMedicineToPharmacyScreen].
 */
@AndroidEntryPoint
class AddMedicineToPharmacyActivity : PharmacistActivity() {

    private val pharmacyId by lazy {
        intent.getLongExtra(PHARMACY_ID, -1)
    }

    @Inject
    lateinit var viewModelFactory: AddMedicineToPharmacyViewModel.Factory
    private val viewModel: AddMedicineToPharmacyViewModel by viewModels<AddMedicineToPharmacyViewModel> {
        AddMedicineToPharmacyViewModel.provideFactory(
            viewModelFactory,
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
            if (viewModel.loadingState == NOT_LOADED)
                viewModel.loadAvailableMedicines(pharmacyId)
            viewModel.startObtainingLocation(this@AddMedicineToPharmacyActivity)
        }

        setContent {
            AddMedicineToPharmacyScreen(
                loadingState = viewModel.loadingState,
                hasLocationPermission = viewModel.hasLocationPermission,
                medicinesState = viewModel.medicinesState,
                onSearch = { viewModel.searchMedicines(it) },
                onMedicineClicked = { medicine ->
                    viewModel.selectedMedicine =
                        if (medicine == viewModel.selectedMedicine)
                            null else medicine
                },
                selectedMedicine = viewModel.selectedMedicine,
                createMedicine = {
                    CreateMedicineActivity.navigateForResult(this, createMedicineResultLauncher)
                },
                addMedicineToPharmacy = { medicineId, stock ->
                    lifecycleScope.launch {
                        val added = viewModel.addMedicineToPharmacy(medicineId, stock)

                        if (added) {
                            val intent = Intent()
                            intent.putExtra(MEDICINE_ID, medicineId)
                            intent.putExtra("quantity", stock)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
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
                putExtra(PHARMACY_ID, pharmacyId)
            }
        }

        fun registerForResult(activity: ComponentActivity, callback: (Long?, Long?) -> Unit) =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) callback(null, null)

                val resultIntent = result.data ?: return@registerForActivityResult
                val medicineId = resultIntent.getLongExtra(MEDICINE_ID, -1)
                val quantity = resultIntent.getLongExtra("quantity", 1)

                callback(medicineId, quantity)
            }
    }

}
