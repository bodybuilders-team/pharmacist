package pt.ulisboa.ist.pharmacist.ui.screens.pharmacy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.LoadingSpinner
import pt.ulisboa.ist.pharmacist.ui.utils.assertNotNull

/**
 * Home screen.
 *
 */
@Composable
fun PharmacyScreen(pharmacy: Pharmacy?, loadingState: PharmacyViewModel.PharmacyLoadingState) {

    if (loadingState == PharmacyViewModel.PharmacyLoadingState.LOADED) {
        assertNotNull(pharmacy)

        PharmacistScreen {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(text = pharmacy.name)

                Text(text = pharmacy.location.toString())

                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Pharmacy picture",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                )
            }
        }
    } else {
        Box {
            LoadingSpinner()
        }
    }
}

@Preview
@Composable
private fun PharmacyScreenPreview() {
    PharmacyScreen(null, PharmacyViewModel.PharmacyLoadingState.LOADING)
}