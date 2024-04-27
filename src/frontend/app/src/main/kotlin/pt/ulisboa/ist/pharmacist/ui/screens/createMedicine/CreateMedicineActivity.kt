package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.hasCameraPermission
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateToForResult
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Activity for the [MedicineScreen].
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

            if (result.data?.extras?.get("data") is Bitmap) {
                handleTakePhoto(result)
            } else {
                handleImageSelection(result)
            }
        }

    private fun handleImageSelection(result: ActivityResult) {
        val uri = result.data?.data

        if (uri == null) {
            Log.e("CreateMedicineActivity", "Failed to get uri")
            return
        }

        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            Log.e("CreateMedicineActivity", "Failed to open input stream")
            return
        }

        val mimeTypeStr = contentResolver.getType(uri)
        val mimeType = mimeTypeStr?.toMediaType()

        if (mimeType == null) {
            Log.e("CreateMedicineActivity", "Failed to get mime type")
            return
        }

        viewModel.uploadBoxPhoto(inputStream.readBytes(), mimeType)
    }

    private fun handleTakePhoto(result: ActivityResult) {
        val imageBitmap = result.data?.extras?.get("data")

        if (imageBitmap !is Bitmap) {
            Log.e("CreateMedicineActivity", "Failed to get image bitmap")
            return
        }

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageBytes = stream.toByteArray()

        viewModel.uploadBoxPhoto(imageBytes, "image/jpeg".toMediaType())
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
                onSelectImage = {
                    val galIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    galIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    galIntent.setType("image/jpeg")

                    val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    val chooser = Intent.createChooser(galIntent, "Some text here")
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(camIntent))

                    imageResultLauncher.launch(chooser)
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
