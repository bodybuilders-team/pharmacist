package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.PharmacistService
import pt.ulisboa.ist.pharmacist.service.connection.isFailure
import pt.ulisboa.ist.pharmacist.service.connection.isSuccess
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


    fun uploadBoxPhoto(boxPhoto: ByteArray) = viewModelScope.launch {
        val createSignedUrlResult = pharmacistService.uploaderService.createSignedUrl()

        if (createSignedUrlResult.isFailure()) {
            return@launch
        }

        Log.d(
            "CreateMedicineModel",
            "Signed URL created successfully with URL: ${createSignedUrlResult.data.signedUrl} and object name: ${createSignedUrlResult.data.objectName}"
        )

        val signedUrl = createSignedUrlResult.data.signedUrl

        val uploadResult = pharmacistService.uploaderService.uploadBoxPhoto(signedUrl, boxPhoto)

        if (uploadResult.isSuccess()) {
            Log.d("CreateMedicineModel", "Box photo uploaded successfully")
        }


    }

    fun createMedicine(name: String, description: String, boxPhotoUrl: String) {

    }

}

