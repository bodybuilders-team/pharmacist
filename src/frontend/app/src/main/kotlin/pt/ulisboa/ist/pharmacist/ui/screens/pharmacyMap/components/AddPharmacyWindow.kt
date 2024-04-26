package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton

@Composable
fun AddPharmacyWindow(
    modifier: Modifier,
    onGoToLocationButtonClick: () -> Unit,
    onAddPictureButtonClick: () -> Unit,
    onAddPharmacyFinishClick: (newPharmacyName: String) -> Unit,
    newPharmacyPhoto: ImageBitmap?
) {
    var newPharmacyName by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .then(modifier)
    ) {
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            Text(
                text = "Pharmacy Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            TextField(
                value = newPharmacyName, // TODO: Add validation
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = { newPharmacyName = it },
                label = { Text("Pharmacy Name*") },
                placeholder = { Text("New Pharmacy") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(250.dp)
                    .padding(8.dp)
            )
            IconTextButton(
                onClick = { onGoToLocationButtonClick() },
                imageVector = Icons.Rounded.LocationOn,
                text = "Go to Location",
                contentDescription = "Go to location",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            if (newPharmacyPhoto != null)
                Box(
                    modifier
                        .height(100.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = newPharmacyPhoto,
                        contentDescription = "Pharmacy photo",
                        modifier = Modifier.padding(2.dp)
                    )
                }
            IconTextButton(
                onClick = { onAddPictureButtonClick() },
                imageVector = Icons.Rounded.CameraAlt,
                text = "Select or Take a picture",
                contentDescription = "Select or Take a picture",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            IconTextButton(
                onClick = {
                    onAddPharmacyFinishClick(newPharmacyName)
                    newPharmacyName = ""
                },
                imageVector = Icons.Rounded.Add,
                text = "Create Pharmacy",
                contentDescription = "Create Pharmacy",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}