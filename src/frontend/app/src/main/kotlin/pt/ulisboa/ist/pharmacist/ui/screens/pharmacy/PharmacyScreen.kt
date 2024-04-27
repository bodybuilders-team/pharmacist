package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineEntry
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyRating
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage

/**
 * Screen that displays the pharmacy information.
 *
 * @param pharmacy The pharmacy to display.
 * @param loadingState The loading state of the pharmacy.
 */
@Composable
fun PharmacyScreen(
    pharmacy: PharmacyWithUserDataModel?,
    loadingState: PharmacyViewModel.PharmacyLoadingState,
    onNavigateToPharmacyClick: (Location) -> Unit,
    medicinesState: Flow<PagingData<MedicineStockModel>>,
    onMedicineClick: (Long) -> Unit,
    onAddMedicineClick: () -> Unit,
    onAddStockClick: (Long) -> Unit,
    onRemoveStockClick: (Long) -> Unit,
    onFavoriteClick: () -> Unit,
    onReportClick: () -> Unit,
    onShareClick: () -> Unit,
    onRatingChanged: (Int) -> Unit
) {
    val medicinesStock = medicinesState.collectAsLazyPagingItems()

    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            if (loadingState == PharmacyViewModel.PharmacyLoadingState.LOADED && pharmacy != null) {
                MeteredAsyncImage(
                    url = pharmacy.pharmacy.pictureUrl,
                    contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(top = 16.dp, bottom = 8.dp)
                )
                Text(
                    text = pharmacy.pharmacy.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = { onNavigateToPharmacyClick(pharmacy.pharmacy.location) }) {
                        Icon(
                            Icons.Rounded.LocationOn,
                            contentDescription = stringResource(R.string.open_in_maps),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            if (pharmacy.userMarkedAsFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(R.string.medicine_addToNotifications_button_description),
                            tint = if (pharmacy.userMarkedAsFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onReportClick) {
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
                    text = "${medicinesStock.itemCount}" + stringResource(R.string.medicines_available),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                )

                Button(
                    onClick = onAddMedicineClick,
                    modifier = Modifier,
                    shape = CircleShape,
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_medicine)
                    )
                    Text(
                        text = stringResource(R.string.add_medicine),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(20.dp)
                ) {

                    items(medicinesStock.itemCount) { index ->
                        val (medicine, stock) = medicinesStock[index]!!
                        PharmacyMedicineEntry(
                            medicine,
                            stock,
                            onMedicineClick = onMedicineClick,
                            onAddStockClick = onAddStockClick,
                            onRemoveStockClick = onRemoveStockClick
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

