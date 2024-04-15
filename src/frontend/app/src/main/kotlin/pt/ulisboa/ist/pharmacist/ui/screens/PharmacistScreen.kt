package pt.ulisboa.ist.pharmacist.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pt.ulisboa.ist.pharmacist.ui.theme.PharmacistTheme

/**
 * A screen that displays the Pharmacist app.
 *
 * @param content the content to be displayed
 */
@Composable
fun PharmacistScreen(content: @Composable () -> Unit) {
    PharmacistTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}
