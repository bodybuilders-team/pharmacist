package pt.ulisboa.ist.pharmacist.ui.screens.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ulisboa.ist.pharmacist.R

private const val STROKE_WIDTH = 4

/**
 * A loading spinner that rotates infinitely.
 * Useful for indicating that a process is running.
 *
 * @param text the text to be shown below the spinner
 * @param modifier the modifier to be applied to the spinner
 */
@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.default_loading_text),
    showText: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator(strokeWidth = STROKE_WIDTH.dp)
        if (showText)
            Text(text = text)
    }
}
