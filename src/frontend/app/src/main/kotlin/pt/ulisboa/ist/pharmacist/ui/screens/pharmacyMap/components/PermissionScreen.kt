package pt.ulisboa.ist.pharmacist.ui.screens.pharmacyMap.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ulisboa.ist.pharmacist.R

/**
 * Screen to request permissions
 *
 * @param onPermissionGranted Callback to be called when permission is granted
 */
@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit,
    permissionRequests: List<String>,
    permissionTitle: String,
    settingsPermissionNote: String,
    settingsPermissionNoteButtonText: String
) {
    var showSettingsPermissionsNote by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedPermissions ->
        var isGranted = true
        grantedPermissions.entries.forEach {
            if (!it.value) {
                isGranted = false
                return@forEach
            }
        }

        if (isGranted) {
            showSettingsPermissionsNote = false
            onPermissionGranted()
        } else {
            showSettingsPermissionsNote = true
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = permissionTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (showSettingsPermissionsNote) {
            Text(
                text = settingsPermissionNote,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
            ) {
                Text(text = settingsPermissionNoteButtonText)
            }
        } else
            Button(
                onClick = {
                    permissionLauncher.launch(permissionRequests.toTypedArray())
                }
            ) {
                Text(text = stringResource(R.string.grant_permission_button))
            }
    }
}
