package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity

/**
 * Activity for the [MedicineSearchScreen].
 */
@AndroidEntryPoint
class MedicineSearchActivity : PharmacistActivity() {

    private val viewModel: MedicineSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.checkForLocationAccessPermission(this)

        lifecycleScope.launch {
            viewModel.obtainLocation(this@MedicineSearchActivity)
        }

        setContent {
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
}
