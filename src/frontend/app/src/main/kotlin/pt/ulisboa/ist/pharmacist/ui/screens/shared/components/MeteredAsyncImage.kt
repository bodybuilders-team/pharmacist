package pt.ulisboa.ist.pharmacist.ui.screens.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.SubcomposeAsyncImage


/**
 * An image that is cached using Coil.
 *
 * @param url the URL of the image
 * @param contentDescription the content description of the image
 * @param modifier the modifier
 */
@Composable
fun CachedImage(
    url: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    return SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                LoadingSpinner(showText = false)
            }
        }
    )
}


