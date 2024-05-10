package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.changeMedicineStock.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateTo
import pt.ulisboa.ist.pharmacist.ui.screens.shared.viewModelInit


/**
 * Activity for the [PharmacyScreen].
 */
class PharmacyActivity : PharmacistActivity() {

    private val pharmacyId by lazy {
        intent.getLongExtra(PHARMACY_ID, -1)
    }

    private val viewModel by viewModelInit {
        PharmacyViewModel(
            dependenciesContainer.pharmacistService,
            dependenciesContainer.sessionManager,
            dependenciesContainer.realTimeUpdatesService,
            pharmacyId
        )
    }

    private val addMedicineResultLauncher = AddMedicineToPharmacyActivity
        .registerForResult(this) { medicineId, quantity ->
            if (medicineId != null && quantity != null) {
                Log.d("PharmacyActivity", "Medicine added: $medicineId, $quantity")
                viewModel.onMedicineAdded(medicineId, quantity)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.listenForRealTimeUpdates()
        if (viewModel.loadingState == PharmacyViewModel.PharmacyLoadingState.NOT_LOADED)
            viewModel.loadPharmacy(pharmacyId)

        setContent {
            PharmacyScreen(
                pharmacy = viewModel.pharmacy,
                loadingState = viewModel.loadingState,
                medicinesList = viewModel.medicinesList,
                onMedicineClick = { medicineId ->
                    MedicineActivity.navigate(this, medicineId)
                },
                onAddMedicineClick = {
                    AddMedicineToPharmacyActivity.navigateForResult(
                        this,
                        addMedicineResultLauncher,
                        pharmacyId
                    )
                },
                onAddStockClick = { medicineId ->
                    viewModel.modifyStock(medicineId, MedicineStockOperation.ADD)
                },
                onRemoveStockClick = { medicineId ->
                    viewModel.modifyStock(medicineId, MedicineStockOperation.REMOVE)
                },
                onFavoriteClick = {
                    viewModel.updateFavoriteStatus()
                },
                onShareClick = {
                    viewModel.pharmacy?.let {
                        lifecycleScope.launch {
                            val imageUri = downloadAndStoreImage()
                            if (imageUri != null) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out this pharmacy!" +
                                                "\n\nName: ${it.pharmacy.name}" +
                                                "\nAddress: https://www.google.com/maps/search/?api=1&query=${it.pharmacy.location.lat},${it.pharmacy.location.lon}" +
                                                (if (it.pharmacy.globalRating != null) "\nRating: ${it.pharmacy.globalRating}⭐" else "") +
                                                "\n\nDownload the Pharmacist app to see more details!"
                                    )
                                    putExtra(Intent.EXTRA_TITLE, "Check out this pharmacy!")
                                    putExtra(Intent.EXTRA_SUBJECT, "Check out this pharmacy!")
                                    putExtra(Intent.EXTRA_STREAM, imageUri)
                                    type = "image/*"
                                }
                                val shareIntent = Intent.createChooser(
                                    sendIntent,
                                    "Share this pharmacy"
                                )
                                startActivity(shareIntent)
                            }
                        }
                    }
                },
                onReportClick = {
                    lifecycleScope.launch {
                        val success = viewModel.updateReportStatus()
                        if (success) {// TODO: Handle error
                            finish()
                        }
                    }
                },
                onRatingChanged = { rating ->
                    viewModel.updateRating(rating)
                }
            )
        }
    }

    /**
     * Downloads and stores the image of the pharmacy.
     * Used to share the pharmacy image.
     */
    private suspend fun downloadAndStoreImage(): Uri? {
        viewModel.downloadImage()
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DISPLAY_NAME, "pharmacy")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (imageUri == null) {
            Log.e("PharmacyActivity", "Failed to create image uri")
            return null
        }

        contentResolver.openOutputStream(imageUri).use { outputStream ->
            if (outputStream == null) {
                Log.e("PharmacyActivity", "Failed to open output stream")
                return null
            }

            val imageBitmap = viewModel.pharmacyImage?.asAndroidBitmap()
            if (imageBitmap == null) {
                Log.e("PharmacyActivity", "Failed to get image bitmap")
                return null
            }

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(imageUri, contentValues, null, null)

        return imageUri
    }

    companion object {
        private const val PHARMACY_ID = "pharmacyId"

        /**
         * Navigates to the [PharmacyActivity].
         *
         * @param context the context from which to navigate
         * @param pharmacyId the id of the pharmacy to navigate to
         */
        fun navigate(context: Context, pharmacyId: Long) {
            context.navigateTo<PharmacyActivity> {
                putExtra(PHARMACY_ID, pharmacyId)
            }
        }
    }
}
