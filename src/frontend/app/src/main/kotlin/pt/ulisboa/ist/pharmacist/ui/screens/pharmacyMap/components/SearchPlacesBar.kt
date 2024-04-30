package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.PharmacyMapViewModel

/**
 * Component to display the search bar for places and the autofill
 *
 * @param locationAutofill the list of locations to display in the autofill
 * @param onSearchPlaces callback to be invoked when the user types in the search bar
 * @param onPlaceClick callback to be invoked when the user clicks on a place in the autofill
 */
@Composable
fun SearchPlacesBar(
    searchQuery: String,
    locationAutofill: List<PharmacyMapViewModel.AutocompleteResult>,
    onSearchPlaces: (String) -> Unit,
    onPlaceClick: (PharmacyMapViewModel.AutocompleteResult) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        TextField(
            value = searchQuery,
            label = { Text(stringResource(R.string.pharmacyMap_searchPlaces_text)) },
            onValueChange = onSearchPlaces,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        AnimatedVisibility(
            visible = locationAutofill.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.dp, 600.dp)
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp),
                )
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(locationAutofill.size) {
                    val autofillEntry = locationAutofill[it]
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            onPlaceClick(autofillEntry)
                        }
                    ) {
                        Text(autofillEntry.address)
                    }
                }
            }
        }
    }
}