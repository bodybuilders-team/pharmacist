package pt.ulisboa.ist.pharmacist.ui.screens.medicine

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
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineViewModel.MedicineLoadingState.NOT_LOADED
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.PharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigateTo

/**
 * Activity for the [MedicineScreen].
 */
@AndroidEntryPoint
class MedicineActivity : PharmacistActivity() {

    private val medicineId by lazy {
        intent.getLongExtra(MEDICINE_ID, -1)
    }

    private val viewModel: MedicineViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<MedicineViewModel.Factory> { factory ->
                factory.create(medicineId)
            }
        }
    )

    companion object {
        const val MEDICINE_ID = "medicineId"

        /**
         * Navigates to the [MedicineActivity].
         *
         * @param context the context from which to navigate
         * @param medicineId the id of the medicine to navigate to
         */
        fun navigate(context: Context, medicineId: Long) {
            context.navigateTo<MedicineActivity> {
                putExtra(MEDICINE_ID, medicineId)
            }
        }

        fun getNavigationIntent(context: Context, medicineId: Long): Intent {
            return Intent(context, MedicineActivity::class.java).apply {
                putExtra(MEDICINE_ID, medicineId)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.loadingState == NOT_LOADED)
            viewModel.loadMedicine()


        viewModel.checkForLocationAccessPermission(this)

        lifecycleScope.launch {
            viewModel.startObtainingLocation(this@MedicineActivity)
        }

        setContent {
            MedicineScreen(
                hasLocationPermission = viewModel.hasLocationPermission,
                medicine = viewModel.medicine,
                loadingState = viewModel.loadingState,
                pharmacies = viewModel.pharmacyPagingFlow.collectAsLazyPagingItems(),
                onPharmacyClick = { pharmacy ->
                    PharmacyActivity.navigate(this, pharmacy.pharmacyId)
                },
                toggleMedicineNotification = {
                    viewModel.toggleMedicineNotification()
                },
                onShareClick = {
                    viewModel.medicine?.let {
                        lifecycleScope.launch {
                            val imageUri = downloadAndStoreImage()
                            if (imageUri != null) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out this medicine!" +
                                                "\n\nName: ${it.name}" +
                                                "\n\nDownload the Pharmacist app to see more details!"
                                    )
                                    putExtra(Intent.EXTRA_TITLE, "Check out this medicine!")
                                    putExtra(Intent.EXTRA_SUBJECT, "Check out this medicine!")
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
                }
            )
        }
    }

    /**
     * Downloads and stores the image of the medicine.
     * Used to share the medicine image.
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

            val imageBitmap = viewModel.medicineImage?.asAndroidBitmap()
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
}
