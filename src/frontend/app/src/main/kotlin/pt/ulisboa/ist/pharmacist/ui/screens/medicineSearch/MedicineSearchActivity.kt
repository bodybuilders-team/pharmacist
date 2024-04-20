package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity

/**
 * Activity for the [MedicineSearchScreen].
 */
class MedicineSearchActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::MedicineSearchViewModel)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedicineSearchScreen(
                medicines = viewModel.medicineSearchData.medicines,
                loadMoreMedicines = {
                    viewModel.loadMoreMedicines()
                },
                onSearch = { viewModel.searchMedicines(it) },
                loadingState = viewModel.medicineSearchData.loadingState
            )
        }
    }


}
