package pt.ulisboa.ist.pharmacist.ui.screens.medicineSearch

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
import pt.ulisboa.ist.pharmacist.service.services.medicines.MedicineWithClosestPharmacyOutputModel
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
    var medicineSearchData by mutableStateOf(
        MedicineSearchData(
            emptyList(),
            NOT_LOADED,
            false
        )
    )
        private set
    private var searchValue by mutableStateOf("")


    fun loadMoreMedicines() {
        if (medicineSearchData.loadingState == LOADING || medicineSearchData.reachedBottomOfQuery)
            return

        Log.d("MEDICINES_SCROLL", "A")
        viewModelScope.launch {
            medicineSearchData = medicineSearchData.copy(loadingState = LOADING)
            Log.d("MEDICINES_SCROLL", "B")

            val result = pharmacistService.medicinesService.getMedicines(
                searchValue,
                "",
                LOAD_MORE_COUNT,
                medicineSearchData.medicines.size.toLong()
            )

            if (result.isSuccess()) {
                if (result.data.medicines.isEmpty()) {
                    medicineSearchData = medicineSearchData.copy(
                        loadingState = LOADED,
                        reachedBottomOfQuery = true
                    )
                    return@launch
                }

                medicineSearchData = medicineSearchData.copy(
                    medicines = medicineSearchData.medicines + result.data.medicines,
                )

                Log.d(
                    "MEDICINES_SCROLL",
                    "Loaded ${result.data.medicines.size} more medicines, total: ${medicineSearchData.medicines.size}"
                )
            }

            medicineSearchData = medicineSearchData.copy(loadingState = LOADED)
            Log.d("MEDICINES_SCROLL", "C")
        }
    }

    fun searchMedicines(searchValue: String) {
        if (medicineSearchData.loadingState == LOADING)
            return

        this.searchValue = searchValue
        medicineSearchData = MedicineSearchData(emptyList(), LOADING, false)
    }


    enum class MedicineLoadingState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    companion object {
        const val LOAD_MORE_COUNT: Long = 2
    }
}

data class MedicineSearchData(
    val medicines: List<MedicineWithClosestPharmacyOutputModel>,
    val loadingState: MedicineSearchViewModel.MedicineLoadingState,
    val reachedBottomOfQuery: Boolean
)