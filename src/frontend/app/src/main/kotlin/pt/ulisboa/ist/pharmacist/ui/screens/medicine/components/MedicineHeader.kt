package pt.ulisboa.ist.pharmacist.ui.screens.medicine.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.MeteredAsyncImage

@Composable
fun MedicineHeader(
    medicine: Medicine,
    toggleMedicineNotification: () -> Unit,
    notificationsActive: Boolean
) {
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(if (isLandscape) 0.5f else 1f)
    ) {
        MeteredAsyncImage(
            url = medicine.boxPhotoUrl,
            contentDescription = stringResource(R.string.medicine_boxPhoto_description),
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.5f else 0.6f)
                .padding(top = 16.dp)
        )

        Text(
            text = medicine.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp),
        )

        Text(
            text = medicine.description,
            style = MaterialTheme.typography.bodyLarge
        )

        IconButton(
            modifier = Modifier,
            onClick = toggleMedicineNotification,
        ) {
            Icon(
                if (notificationsActive) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsOff,
                contentDescription = stringResource(R.string.medicine_addToNotifications_button_description),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}