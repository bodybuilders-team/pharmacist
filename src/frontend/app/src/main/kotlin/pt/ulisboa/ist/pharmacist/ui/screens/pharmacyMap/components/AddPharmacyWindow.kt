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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
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
                text = stringResource(R.string.pharmacy_details),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            TextField(
                value = newPharmacyName, // TODO: Add validation
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = { newPharmacyName = it },
                label = { Text(stringResource(R.string.pharmacy_name)) },
                placeholder = { Text(stringResource(R.string.new_pharmacy)) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(250.dp)
                    .padding(8.dp)
            )
            IconTextButton(
                onClick = { onGoToLocationButtonClick() },
                imageVector = Icons.Rounded.LocationOn,
                text = stringResource(R.string.go_to_location),
                contentDescription = stringResource(R.string.go_to_location),
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
                        contentDescription = stringResource(R.string.pharmacyMap_pharmacyPicture_description),
                        modifier = Modifier.padding(2.dp)
                    )
                }
            IconTextButton(
                onClick = { onAddPictureButtonClick() },
                imageVector = Icons.Rounded.CameraAlt,
                text = stringResource(R.string.take_photo_select_image),
                contentDescription = stringResource(R.string.take_photo_select_image),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            IconTextButton(
                onClick = {
                    onAddPharmacyFinishClick(newPharmacyName)
                    newPharmacyName = ""
                },
                imageVector = Icons.Rounded.Add,
                text = stringResource(R.string.create_pharmacy),
                contentDescription = stringResource(R.string.create_pharmacy),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}