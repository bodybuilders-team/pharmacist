package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen
import pt.ulisboa.ist.pharmacist.ui.screens.createMedicine.components.CreateMedicineImage
import pt.ulisboa.ist.pharmacist.ui.screens.createMedicine.components.CreateMedicineNameAndDescription
import pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components.PermissionScreen
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.IconTextButton
import pt.ulisboa.ist.pharmacist.ui.screens.shared.components.ScreenTitle


/**
 * Create Medicine screen.
 *
 * @param boxPhoto the box photo
 * @param onAddPictureButtonClick function to be executed when the user selects an image
 * @param onCreateMedicine function to be executed when the user creates a medicine
 */
@Composable
fun CreateMedicineScreen(
    hasCameraPermission: Boolean,
    boxPhoto: ImageBitmap?,
    onAddPictureButtonClick: () -> Unit,
    onCreateMedicine: (String, String) -> Unit
) {
    var _hasCameraPermission by remember { mutableStateOf(hasCameraPermission) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    PharmacistScreen {
        if (!_hasCameraPermission) {
            PermissionScreen(
                onPermissionGranted = { _hasCameraPermission = true },
                permissionRequests = listOf(android.Manifest.permission.CAMERA),
                permissionTitle = stringResource(R.string.camera_permission),
                settingsPermissionNote = stringResource(R.string.camera_settings_permission_note),
                settingsPermissionNoteButtonText = stringResource(R.string.camera_settings_permission_note_button_text)
            )

            return@PharmacistScreen
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            var name by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }

            ScreenTitle(title = stringResource(R.string.create_medicine))

            if (isLandscape)
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CreateMedicineImage(boxPhoto, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CreateMedicineNameAndDescription(
                            name,
                            onNameChange = { name = it },
                            description,
                            onDescriptionChange = { description = it }
                        )
                        IconButton(
                            onClick = onAddPictureButtonClick,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Image,
                                contentDescription = stringResource(R.string.take_photo_select_image),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            else
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CreateMedicineNameAndDescription(
                        name,
                        onNameChange = { name = it },
                        description,
                        onDescriptionChange = { description = it }
                    )

                    CreateMedicineImage(boxPhoto)

                    IconButton(
                        onClick = onAddPictureButtonClick,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Image,
                            contentDescription = stringResource(R.string.take_photo_select_image),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

            IconTextButton(
                enabled = name.isNotBlank() && description.isNotBlank() && boxPhoto != null,
                onClick = { onCreateMedicine(name, description) },
                imageVector = Icons.Rounded.Add,
                text = stringResource(R.string.create_medicine),
                contentDescription = stringResource(R.string.create_medicine),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

