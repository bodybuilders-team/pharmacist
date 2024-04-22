package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isFailure
import pt.ulisboa.ist.pharmacist.session.SessionManager
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistViewModel


/**
 * View model for the [CreateMedicineActivity].
 *
 * @property pharmacistService the service used to handle the pharmacist game
 * @property sessionManager the manager used to handle the user session
 */
class CreateMedicineModel(
    pharmacistService: PharmacistService,
    sessionManager: SessionManager
) : PharmacistViewModel(pharmacistService, sessionManager) {

    var boxPhotoUrl by mutableStateOf<String?>(null)
    var boxPhoto by mutableStateOf<ImageBitmap?>(null)
    var state by mutableStateOf(CreateMedicineState.NOT_STARTED)

    fun uploadBoxPhoto(boxPhotoData: ByteArray, mediaType: MediaType) = viewModelScope.launch {
        val createSignedUrlResult =
            pharmacistService.uploaderService.createSignedUrl(mediaType.toString())

        if (createSignedUrlResult.isFailure()) {
            Log.e("CreateMedicineModel", "Failed to create signed URL")
            return@launch
        }

        Log.d(
            "CreateMedicineModel",
            "Signed URL created successfully with URL: ${createSignedUrlResult.data.signedUrl} and object name: ${createSignedUrlResult.data.url}"
        )

        val signedUrl = createSignedUrlResult.data.signedUrl


        val uploadResult =
            pharmacistService.uploaderService.uploadBoxPhoto(signedUrl, boxPhotoData, mediaType)

        if (uploadResult.isFailure()) {
            Log.e("CreateMedicineModel", "Failed to upload box photo")
            return@launch
        }

        Log.d("CreateMedicineModel", "Box photo uploaded successfully")
        boxPhotoUrl = createSignedUrlResult.data.url

        boxPhoto = BitmapFactory.decodeByteArray(boxPhotoData, 0, boxPhotoData.size).asImageBitmap()
    }

    suspend fun createMedicine(name: String, description: String): Long? {
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
            pharmacistService.medicinesService.createMedicine(name, description, boxPhotoUrl!!)

        if (createMedicineResult.isFailure()) {
            Log.e("CreateMedicineModel", "Failed to create medicine")
            return null
        }

        Log.d("CreateMedicineModel", "Medicine created successfully")
        state = CreateMedicineState.MEDICINE_CREATED

        return createMedicineResult.data.medicineId
    }


    enum class CreateMedicineState {
        NOT_STARTED,
        CREATING_MEDICINE,
        MEDICINE_CREATED
    }

}

