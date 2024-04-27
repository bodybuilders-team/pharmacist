package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.changeMedicineStock.MedicineStockOperation
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistActivity
import pt.ulisboa.ist.pharmacist.ui.screens.addMedicineToPharmacy.AddMedicineToPharmacyActivity
import pt.ulisboa.ist.pharmacist.ui.screens.medicine.MedicineActivity
import pt.ulisboa.ist.pharmacist.ui.screens.shared.navigation.navigateTo
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
            pharmacyId
        )
    }

    private val addPharmacyResultLauncher = AddMedicineToPharmacyActivity
        .registerForResult(this) { medicineId ->
            if (medicineId != null)
                viewModel.addMedicine(medicineId)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.loadingState == PharmacyViewModel.PharmacyLoadingState.NOT_LOADED)
            viewModel.loadPharmacy(pharmacyId)

        setContent {
            PharmacyScreen(
                pharmacy = viewModel.pharmacy,
                loadingState = viewModel.loadingState,
                onNavigateToPharmacyClick = { location ->
                    val gmmIntentUri =
                        Uri.parse("geo:0,0?q=${location.lat},${location.lon}(${viewModel.pharmacy?.pharmacy?.name})")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    startActivity(mapIntent)
                },
                medicinesState = viewModel.medicinesState,
                onMedicineClick = { medicineId ->
                    MedicineActivity.navigate(this, medicineId)
                },
                onAddMedicineClick = {
                    AddMedicineToPharmacyActivity.navigateForResult(
                        this,
                        addPharmacyResultLauncher,
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
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TITLE, it.pharmacy.name)
                            putExtra(
                                Intent.EXTRA_TEXT,
                                PHARMACY_SHARE_TEXT + " " + it.pharmacy.pictureUrl
                            )
                            setDataAndType(Uri.parse(it.pharmacy.pictureUrl), "image/*")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                },
                onReportClick = {
                    viewModel.updateReportStatus()
                },
                onRatingChanged = { rating ->
                    viewModel.updateRating(rating)
                }
            )
        }
    }

    companion object {
        private const val PHARMACY_ID = "pharmacyId"
        private const val PHARMACY_SHARE_TEXT = "Check out this pharmacy: "

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
