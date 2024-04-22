package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.ui.screens.PharmacistScreen


/**
 * Create Medicine screen.
 *
 */
@Composable
fun CreateMedicineScreen(
    boxPhoto: ImageBitmap?,
    onSelectImage: () -> Unit,
    onCreateMedicine: (String, String) -> Unit
) {
    PharmacistScreen {
        // Add take photo button or image picker
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            var name by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.padding(16.dp)
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.padding(16.dp)
            )

            Row {
                if (boxPhoto != null)
                    Image(
                        bitmap = boxPhoto,
                        contentDescription = "Box photo",
                        modifier = Modifier.padding(16.dp)
                    )

                IconButton(
                    onClick = onSelectImage,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Rounded.Image, contentDescription = "Take photo/Select image")
                }
            }

            Button(onClick = { onCreateMedicine(name, description) }) {
                Text("Create")
            }

        }
    }
}

