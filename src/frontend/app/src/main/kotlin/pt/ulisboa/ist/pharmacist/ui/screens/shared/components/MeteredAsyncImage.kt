package pt.ulisboa.ist.pharmacist.ui.screens.shared.components

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

/**
 * Checks if the user is on a metered connection.
 *
 * @param context the context
 * @return true if the user is on a metered connection, false otherwise
 */
fun isOnMeteredConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.isActiveNetworkMetered
}

/**
 * An image that only loads if the user is not on a metered connection.
 *
 * @param url the URL of the image
 * @param contentDescription the content description of the image
 * @param modifier the modifier
 */
@Composable
fun MeteredAsyncImage(
    url: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isOnMeteredConnection = remember {
        isOnMeteredConnection(context = context)
    }

    return if (isOnMeteredConnection) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            LoadingSpinner(showText = false)
        }
    } else
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier
        )
}


