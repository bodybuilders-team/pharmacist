package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconButton

@Composable
fun AddPharmacyWindow(
    modifier: Modifier,
    onGoToLocationButtonClick: () -> Unit,
    onAddPictureButtonClick: () -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String, newPharmacyDescription: String) -> Unit
) {

    var newPharmacyName by rememberSaveable { mutableStateOf("") }
    var newPharmacyDescription by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .then(modifier)
    ) {
        Column {
            IconButton(
                onClick = { onGoToLocationButtonClick() },
                imageVector = Icons.Rounded.LocationOn,
                text = "Go to location",
                contentDescription = "Go to location"
            )
            TextField(
                value = newPharmacyName,
                textStyle = MaterialTheme.typography.bodyMedium,
                //fontWeight = FontWeight.Bold,
                onValueChange = { newPharmacyName = it },
                label = { Text("Pharmacy Name") },
                placeholder = { Text("New Pharmacy") }
            )
            TextField(
                value = newPharmacyDescription,
                textStyle = MaterialTheme.typography.bodySmall,
                onValueChange = { newPharmacyDescription = it },
                label = { Text("Pharmacy Description") },
                placeholder = { Text("No description") }
            )
            IconButton(
                onClick = { onAddPictureButtonClick() },
                imageVector = Icons.Rounded.CameraAlt,
                text = "Select or Take a picture",
                contentDescription = "Select or Take a picture"
            )
            Button(
                onClick = {
                    onAddPharmacyFinishClick(newPharmacyName, newPharmacyDescription)
                    newPharmacyName = ""
                    newPharmacyDescription = ""
                }
            ) {
                Text("Finish")
            }
        }
    }
}