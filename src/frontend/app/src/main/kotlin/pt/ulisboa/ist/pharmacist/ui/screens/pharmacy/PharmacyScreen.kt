package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyHeader
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineList
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyRating
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner

/**
 * Screen that displays the pharmacy information.
 *
 * @param pharmacy The pharmacy to display.
 * @param loadingState The loading state of the pharmacy.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PharmacyScreen(
    pharmacy: PharmacyWithUserDataModel?,
    loadingState: PharmacyViewModel.PharmacyLoadingState,
    medicinesList: SnapshotStateMap<Long, MedicineStockModel>,
    onMedicineClick: (Long) -> Unit,
    onAddMedicineClick: () -> Unit,
    onAddStockClick: (Long) -> Unit,
    onRemoveStockClick: (Long) -> Unit,
    onFavoriteClick: () -> Unit,
    onReportClick: () -> Unit,
    onShareClick: () -> Unit,
    onRatingChanged: (Int) -> Unit
) {
    val medicinesStock = medicinesList.values.toList()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val pagerState = rememberPagerState(initialPage = 0)

    PharmacistScreen {
        if (loadingState == PharmacyViewModel.PharmacyLoadingState.LOADED && pharmacy != null)
            if (isLandscape)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    PharmacyHeader(
                        pagerState,
                        pharmacy,
                        onFavoriteClick,
                        onReportClick,
                        onShareClick,
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .fillMaxHeight()
                    )
                    PharmacyRating(
                        pharmacy = pharmacy,
                        onRatingChanged = onRatingChanged,
                        modifier = Modifier.fillMaxWidth(0.3f)
                    )
                    PharmacyMedicineList(
                        medicinesStock,
                        onAddMedicineClick,
                        onMedicineClick,
                        onAddStockClick,
                        onRemoveStockClick
                    )
                }
            else
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    PharmacyHeader(
                        pagerState,
                        pharmacy,
                        onFavoriteClick,
                        onReportClick,
                        onShareClick,
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                    PharmacyRating(
                        pharmacy = pharmacy,
                        onRatingChanged = onRatingChanged,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    PharmacyMedicineList(
                        medicinesStock,
                        onAddMedicineClick,
                        onMedicineClick,
                        onAddStockClick,
                        onRemoveStockClick
                    )
                }
        else
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingSpinner(text = stringResource(R.string.loading_pharmacy))
            }
    }
}
