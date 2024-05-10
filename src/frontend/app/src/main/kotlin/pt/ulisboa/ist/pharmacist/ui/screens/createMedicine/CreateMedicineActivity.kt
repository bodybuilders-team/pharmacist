package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.ImageHandlingUtils
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasCameraPermission
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateToForResult
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the [CreateMedicine].
 */
class CreateMedicineActivity : PharmacistActivity() {

    private val viewModel by viewModelInit {
        CreateMedicineViewModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager
        )
    }

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            if (result.data?.extras?.get("data") is Bitmap)
                handleTakePhoto(result)
            else
                handleImageSelection(result)
        }

    private fun handleImageSelection(result: ActivityResult) {
        ImageHandlingUtils.handleImageSelection(contentResolver, result)
            ?.let { (inputStream, mediaType) ->
                viewModel.uploadBoxPhoto(inputStream.readBytes(), mediaType)
            }
    }

    private fun handleTakePhoto(result: ActivityResult) {
        ImageHandlingUtils.handleTakePhoto(result)
            ?.let { (boxPhotoData, mediaType) ->
                viewModel.uploadBoxPhoto(boxPhotoData, mediaType)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.hasCameraPermission = hasCameraPermission()

        setContent {
            CreateMedicineScreen(
                hasCameraPermission = viewModel.hasCameraPermission,
                boxPhoto = viewModel.boxPhoto,
                onCreateMedicine = { name, description ->
                    lifecycleScope.launch {
                        val mid = viewModel.createMedicine(name, description)
                        if (mid != null) {
                            val resultIntent = Intent()
                            resultIntent.putExtra(MEDICINE_ID, mid)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    }
                },
                onAddPictureButtonClick = {
                    imageResultLauncher.launch(ImageHandlingUtils.getChooserIntent())
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.hasCameraPermission = hasCameraPermission()
    }


    companion object {
        private const val MEDICINE_ID = "medicineId"

        fun navigateForResult(
            context: Context,
            resultLauncher: ActivityResultLauncher<Intent>
        ) {
            context.navigateToForResult<CreateMedicineActivity>(resultLauncher)
        }

        fun registerForResult(activity: ComponentActivity, callback: (Long?) -> Unit) =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) callback(null)

                val resultIntent = result.data ?: return@registerForActivityResult
                val medicineId = resultIntent.getLongExtra(MEDICINE_ID, -1)

                callback(medicineId)
            }
    }

}
