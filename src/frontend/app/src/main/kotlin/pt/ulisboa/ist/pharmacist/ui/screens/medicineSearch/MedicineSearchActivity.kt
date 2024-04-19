package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchViewModel.MedicineLoadingState.LOADING

/**
 * Activity for the [MedicineScreen].
 */
class MedicineSearchActivity : PharmacistActivity() {

    private val viewModel by getViewModel(::MedicineSearchViewModel)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.items.isEmpty() && viewModel.loadingState != LOADING) {
            for (i in 0 until 10)
                viewModel.loadMoreMedicines()
        }

        setContent {
            MedicineScreen(
                medicines = viewModel.items,
                loadMoreMedicines = {
                    viewModel.loadMoreMedicines()
                },
                loadingState = viewModel.loadingState
            )
        }
    }


}
