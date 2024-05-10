package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isFailure
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils


/**
 * View model for the [CreateMedicineActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 */
class CreateMedicineViewModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {
    var hasCameraPermission by mutableStateOf(false)
    private var boxPhotoUrl by mutableStateOf<String?>(null)
    var boxPhoto by mutableStateOf<ImageBitmap?>(null)
    var state by mutableStateOf(CreateMedicineState.NOT_STARTED)

    fun uploadBoxPhoto(boxPhotoData: ByteArray, mediaType: MediaType) = viewModelScope.launch {
        ImageHandlingUtils.uploadBoxPhoto(boxPhotoData, mediaType, pharmacistService)
            ?.let {
                boxPhotoUrl = it.boxPhotoUrl
                boxPhoto = it.boxPhoto
            }
    }

    suspend fun createMedicine(name: String, description: String): Long? {
        val boxPhotoUrl = boxPhotoUrl
        if (boxPhotoUrl == null) {
            Log.e("CreateMedicineModel", "Box photo URL is null")
            return null
        }
        if (name == "" || description == "") {
            Log.e("CreateMedicineModel", "Name and description must not be empty")
            return null
        }

        state = CreateMedicineState.CREATING_MEDICINE

        val createMedicineResult =
            pharmacistService.medicinesService.createMedicine(name, description, boxPhotoUrl)

        if (createMedicineResult.isFailure()) {
            Log.e("CreateMedicineModel", "Failed to create medicine")
            return null
        }

        Log.d("CreateMedicineModel", "Medicine created successfully")
        state = CreateMedicineState.MEDICINE_CREATED

        boxPhoto = null
        this.boxPhotoUrl = null
        return createMedicineResult.data.medicineId
    }


    enum class CreateMedicineState {
        NOT_STARTED,
        CREATING_MEDICINE,
        MEDICINE_CREATED
    }

}

