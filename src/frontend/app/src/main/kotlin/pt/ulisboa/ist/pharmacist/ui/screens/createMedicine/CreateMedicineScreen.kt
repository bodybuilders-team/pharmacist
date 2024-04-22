package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen


/**
 * Create Medicine screen.
 *
 */
@Composable
fun CreateMedicineScreen(
    onTakePhoto: () -> Unit
) {

    PharmacistScreen {
        // Add take photo button or image picker
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Take a photo of the medicine box",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
            IconButton(
                onClick = {
                    onTakePhoto()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(Icons.Rounded.Notifications, contentDescription = "Take photo")
            }
        }
    }
}

