package pt.ulisboa.ist.pharmacist.ui.screens.addPharmacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen


/**
 * Add Pharmacy screen.
 *
 * @param loadingState the current state of the loading operation
 */
@Composable
fun AddPharmacyScreen(
    loadingState: AddPharmacyViewModel.AddPharmacyLoadingState
) {
    PharmacistScreen {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // TODO: Add pharmacy details here
        }
    }
}

@Preview
@Composable
private fun AddPharmacyScreenPreview() {
    AddPharmacyScreen(
        loadingState = AddPharmacyViewModel.AddPharmacyLoadingState.LOADED
    )
}