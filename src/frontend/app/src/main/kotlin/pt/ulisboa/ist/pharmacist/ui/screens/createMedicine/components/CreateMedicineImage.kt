package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R

@Composable
fun CreateMedicineImage(boxPhoto: ImageBitmap?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (boxPhoto != null) {
            Image(
                bitmap = boxPhoto,
                contentDescription = stringResource(R.string.medicine_boxPhoto_description),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(0.6f)
            )
        }
    }
}