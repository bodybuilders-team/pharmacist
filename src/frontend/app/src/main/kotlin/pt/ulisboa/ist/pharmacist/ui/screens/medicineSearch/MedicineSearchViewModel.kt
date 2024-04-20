package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchViewModel.MedicineLoadingState.LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchViewModel.MedicineLoadingState.LOADING
import pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch.MedicineSearchViewModel.MedicineLoadingState.NOT_LOADED

/**
 * View model for the [MedicineSearchActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 *
 * @property loadingState the current loading state of the view model
 */
class MedicineSearchViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var loadingState by mutableStateOf(NOT_LOADED)
        private set

    var items: List<Medicine> by mutableStateOf(emptyList())
        private set

    fun loadMoreMedicines() = viewModelScope.launch {
        loadingState = LOADING

//        val result = pharmacistService.medicinesService.getMedicines(LOAD_MORE_COUNT, items.size.toLong())
//        if(result.isSuccess())
//            items = items + result.data
        Log.d("Medicines", items.toString())
        delay(1000)

        items += listOf(
            Medicine(
                id = 1L + items.size,
                name = "Medicine " + (1 + items.size),
                purpose = "Purpose 1",
                boxPhoto = "boxPhoto 1"
            ),
            Medicine(
                id = 2L + items.size,
                name = "Medicine " + (2 + items.size),
                purpose = "Purpose 2",
                boxPhoto = "boxPhoto 2"
            ),
            Medicine(
                id = 3L + items.size,
                name = "Medicine " + (3 + items.size),
                purpose = "Purpose 3",
                boxPhoto = "boxPhoto 3"
            ),
        )

        loadingState = LOADED
    }


    enum class MedicineLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    companion object {
        const val LOAD_MORE_COUNT: Long = 3
    }

}
