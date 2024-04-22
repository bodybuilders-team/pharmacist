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
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.listAvailableMedicines.MedicineStockModel
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.PharmacyMedicineEntry
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacy.components.StarRatingBar
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.utils.MeteredAsyncImage

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
    onRatingChanged: (Int) -> Unit
) {
    val medicinesStock = medicinesState.collectAsLazyPagingItems()

    if (loadingState == PharmacyViewModel.PharmacyLoadingState.LOADED && pharmacy != null) {
        PharmacistScreen {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                MeteredAsyncImage(
                    url = pharmacy.pharmacy.pictureUrl,
                    contentDescription = "Pharmacy picture",
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(top = 16.dp)
                )

                Row {
                    Text(
                        text = pharmacy.pharmacy.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(8.dp),
                    )
                    IconButton(
                        modifier = Modifier,
                        onClick = onFavoriteClick,
                    ) {
                        Icon(
                            if (pharmacy.userMarkedAsFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Add to notifications",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToPharmacyClick(pharmacy.pharmacy.location) },
                ) {
                    Row {
                        Icon(
                            Icons.Rounded.LocationOn,
                            contentDescription = "Open in maps",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${pharmacy.pharmacy.location}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                StarRatingBar(
                    rating = pharmacy.userRating ?: 0,
                    onRatingChanged = onRatingChanged,
                )

                Text(
                    text = "${medicinesStock.itemCount} medicines available",
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
                        contentDescription = "Add medicine"
                    )
                    Text(
                        text = "Add New Medicine",
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
            }
        }
    } else
        Box {
            LoadingSpinner()
        }
}
