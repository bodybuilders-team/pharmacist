package pt.ulisboa.ist.pharmacist.ui.screens.createMedicine.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R

@Composable
fun CreateMedicineNameAndDescription(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column {
        // TODO: Add validation of these fields
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.medicine_name)) },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.medicine_description)) },
            modifier = Modifier.padding(16.dp)
        )
    }
}