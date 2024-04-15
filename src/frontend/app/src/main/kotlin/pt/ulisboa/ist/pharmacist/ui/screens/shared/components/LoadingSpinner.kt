package pt.ulisboa.ist.pharmacist.ui.screens.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R

private const val STROKE_WIDTH = 4

/**
 * A loading spinner that rotates infinitely.
 * Useful for indicating that a process is running.
 *
 * @param text the text to be shown below the spinner
 */
@Composable
fun LoadingSpinner(text: String = stringResource(R.string.defaultLoading_text)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(strokeWidth = STROKE_WIDTH.dp)
        Text(text = text)
    }
}
