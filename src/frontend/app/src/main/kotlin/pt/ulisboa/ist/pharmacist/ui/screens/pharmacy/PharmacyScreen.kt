package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyHeader
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineList
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyRating
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton
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
    pharmacy: Pharmacy?,
    loadingState: PharmacyViewModel.PharmacyLoadingState,
    medicineList: LazyPagingItems<MedicineStock>,
    onMedicineClick: (Long) -> Unit,
    onAddMedicineClick: () -> Unit,
    onAddStockClick: (Long) -> Unit,
    onRemoveStockClick: (Long) -> Unit,
    onFavoriteClick: () -> Unit,
    onReportClick: () -> Unit,
    onShareClick: () -> Unit,
    onRatingChanged: (Int) -> Unit
) {
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val pagerState = rememberPagerState(initialPage = 0)
    var flaggingPharmacy: Boolean by remember { mutableStateOf(false) }

    PharmacistScreen {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (loadingState.isLoaded() && pharmacy != null) {
                if (flaggingPharmacy)
                    AlertDialog(
                        onDismissRequest = { flaggingPharmacy = false },
                        title = {
                            Text(
                                text = "Flagging Pharmacy",
                                color = if (isSystemInDarkTheme()) Color.Black else Color.Black
                            )
                        },
                        text = {
                            Text(
                                text = "Are you sure you want to flag this pharmacy? This action is irreversible.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            IconTextButton(
                                onClick = {
                                    flaggingPharmacy = false
                                    onReportClick()
                                },
                                imageVector = Icons.Rounded.Flag,
                                contentDescription = "Flag",
                                text = "Yes"
                            )
                        },
                        dismissButton = {
                            IconTextButton(
                                onClick = {
                                    flaggingPharmacy = false
                                },
                                imageVector = Icons.Rounded.Cancel,
                                contentDescription = "Cancel",
                                text = "Cancel"
                            )
                        },
                        backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )

                if (isLandscape)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        PharmacyHeader(
                            pagerState,
                            pharmacy,
                            onFavoriteClick,
                            onReportClick = {
                                flaggingPharmacy = true
                            },
                            onShareClick,
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .fillMaxHeight()
                        )
                        PharmacyRating(
                            pharmacy = pharmacy,
                            onRatingChanged = onRatingChanged,
                            modifier = Modifier.fillMaxWidth(0.35f)
                        )
                        PharmacyMedicineList(
                            medicineList = medicineList,
                            onAddMedicineClick = onAddMedicineClick,
                            onMedicineClick = onMedicineClick,
                            onAddStockClick = onAddStockClick,
                            onRemoveStockClick = onRemoveStockClick
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
                            onReportClick = {
                                flaggingPharmacy = true
                            },
                            onShareClick,
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                        PharmacyRating(
                            pharmacy = pharmacy,
                            onRatingChanged = onRatingChanged,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                        PharmacyMedicineList(
                            medicineList = medicineList,
                            onAddMedicineClick = onAddMedicineClick,
                            onMedicineClick = onMedicineClick,
                            onAddStockClick = onAddStockClick,
                            onRemoveStockClick = onRemoveStockClick
                        )
                    }
            } else
                LoadingSpinner(text = stringResource(R.string.loading_pharmacy))
        }
    }
}
