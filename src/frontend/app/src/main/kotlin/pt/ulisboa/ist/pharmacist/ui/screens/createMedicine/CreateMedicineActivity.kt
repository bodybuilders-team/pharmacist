package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.InputStream
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateToForResult
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit

/**
 * Activity for the [MedicineScreen].
 */
class CreateMedicineActivity : PharmacistActivity() {

    private val viewModel by viewModelInit {
        CreateMedicineModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager
        )
    }

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            val uri = result.data?.data ?: return@registerForActivityResult

            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            if (inputStream == null) {
                Log.e("CreateMedicineActivity", "Failed to open input stream")
                return@registerForActivityResult
            }

            viewModel.uploadBoxPhoto(inputStream.readBytes())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CreateMedicineScreen(
                onTakePhoto = {
                    val galIntent = Intent(Intent.ACTION_GET_CONTENT)
                    galIntent.type = "image/*"

                    val camIntent = Intent("android.media.action.IMAGE_CAPTURE")

                    val chooser = Intent.createChooser(galIntent, "Some text here")
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(camIntent))

                    imageResultLauncher.launch(chooser)
                }
            )
        }
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
