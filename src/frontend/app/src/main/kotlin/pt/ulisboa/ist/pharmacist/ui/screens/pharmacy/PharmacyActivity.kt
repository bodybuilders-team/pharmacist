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
import androidx.activity.viewModels
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateTo


/**
 * Activity for the [PharmacyScreen].
 */
@AndroidEntryPoint
class PharmacyActivity : PharmacistActivity() {

    private val pharmacyId by lazy {
        intent.getLongExtra(PHARMACY_ID, -1)
    }

    private val viewModel: PharmacyViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<PharmacyViewModel.Factory> { factory ->
                factory.create(pharmacyId)
            }
        }
    )

    private val addMedicineResultLauncher = AddMedicineToPharmacyActivity
        .registerForResult(this) { medicineId, quantity ->
            if (medicineId != null && quantity != null) {
                Log.d("PharmacyActivity", "Medicine added: $medicineId, $quantity")
                viewModel.onMedicineAdded(medicineId, quantity)
            }
        }

    override fun onResume() {
        super.onResume()

        Log.d("PharmacyActivity", "Loading pharmacy")
        viewModel.loadPharmacy(pharmacyId)
        viewModel.invalidate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("PharmacyActivity", "Pharmacy ID: $pharmacyId")

        viewModel.listenForRealTimeUpdates()

        if (viewModel.loadingState == PharmacyViewModel.PharmacyLoadingState.NOT_LOADED) {
            Log.d("PharmacyActivity", "Loading pharmacy")
            viewModel.loadPharmacy(pharmacyId)
        }

        setContent {
            PharmacyScreen(
                pharmacy = viewModel.pharmacy,
                loadingState = viewModel.loadingState,
                medicineList = viewModel.medicinePagingFlow.collectAsLazyPagingItems(),
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
                                                "\n\nName: ${it.name}" +
                                                "\nAddress: https://www.google.com/maps/search/?api=1&query=${it.location.lat},${it.location.lon}" +
                                                (if (it.globalRating != null) "\nRating: ${it.globalRating}⭐" else "") +
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

        fun getNavigationIntent(applicationContext: Context, pharmacyId: Long): Intent {
            return Intent(applicationContext, PharmacyActivity::class.java).apply {
                putExtra(PHARMACY_ID, pharmacyId)
            }
        }
    }
}
