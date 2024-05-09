package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineEntry
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyRating
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage
import pt.ulisboa.ist.pharmacist.ui.theme.Favorite

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

    val pagerState = rememberPagerState(initialPage = 0)

    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            if (loadingState == PharmacyViewModel.PharmacyLoadingState.LOADED && pharmacy != null) {
                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(top = 16.dp, bottom = 8.dp)
                        .height(200.dp),
                ) { page ->
                    if (page == 0)
                        MeteredAsyncImage(
                            url = pharmacy.pharmacy.pictureUrl,
                            contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
                            modifier = Modifier.fillMaxSize()
                        )
                    else
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = CameraPositionState(
                                position = CameraPosition(
                                    pharmacy.pharmacy.location.toLatLng(),
                                    15f,
                                    0f,
                                    0f
                                )
                            ),
                            uiSettings = MapUiSettings(
                                myLocationButtonEnabled = false,
                                rotationGesturesEnabled = false,
                                scrollGesturesEnabled = false,
                                tiltGesturesEnabled = false
                            )
                        ) {
                            Marker(
                                state = MarkerState(position = pharmacy.pharmacy.location.toLatLng()),
                                title = pharmacy.pharmacy.name
                            )
                        }
                }
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                )

                Text(
                    text = pharmacy.pharmacy.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            if (pharmacy.userMarkedAsFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(R.string.medicine_addToNotifications_button_description),
                            tint = if (pharmacy.userMarkedAsFavorite) Favorite else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onReportClick) { // TODO: After report, show a dialog to confirm the report and then return to the pharmacy map
                        Icon(
                            if (pharmacy.userFlagged) Icons.Rounded.Flag else Icons.Rounded.OutlinedFlag,
                            contentDescription = stringResource(R.string.report_pharmacy),
                            tint = Color.Red
                        )
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                PharmacyRating(
                    pharmacy = pharmacy,
                    onRatingChanged = onRatingChanged,
                )

                Text(
                    text = "${medicinesStock.size} " +
                            stringResource(
                                if (medicinesStock.size != 1)
                                    R.string.medicines_available
                                else R.string.medicine_available
                            ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )

                IconTextButton(
                    imageVector = Icons.Rounded.Medication,
                    text = stringResource(R.string.add_medicine),
                    contentDescription = stringResource(R.string.add_medicine),
                    onClick = onAddMedicineClick
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    items(medicinesStock.size) { index ->
                        val (medicine, stock) = medicinesStock[index]
                        PharmacyMedicineEntry(
                            medicine,
                            stock,
                            onMedicineClick = onMedicineClick,
                            onAddStockClick = onAddStockClick,
                            onRemoveStockClick = onRemoveStockClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else
                Box {
                    LoadingSpinner()
                }
        }
    }
}

