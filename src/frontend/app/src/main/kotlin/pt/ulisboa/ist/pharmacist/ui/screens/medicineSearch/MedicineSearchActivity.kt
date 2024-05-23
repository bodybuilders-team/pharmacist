package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasLocationPermission

/**
 * Activity for the [MedicineSearchScreen].
 */
@AndroidEntryPoint
class MedicineSearchActivity : PharmacistActivity() {

    private val viewModel: MedicineSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.hasLocationPermission = hasLocationPermission()

        setContent {
            LaunchedEffect(key1 = viewModel.hasLocationPermission) {
                if (viewModel.hasLocationPermission)
                    viewModel.obtainLocation(this@MedicineSearchActivity)
            }

            MedicineSearchScreen(
                hasLocationPermission = viewModel.hasLocationPermission,
                medicinePagingItems = viewModel.medicinePagingFlow?.collectAsLazyPagingItems(),
                onSearch = { viewModel.searchMedicines(it) },
                onMedicineClicked = { medicine ->
                    MedicineActivity.navigate(this, medicine.medicineId)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.hasLocationPermission = hasLocationPermission()
    }
}
