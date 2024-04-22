package pt.ulisboa.ist.pharmacist.ui.screens.shared.components

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import pt.ulisboa.ist.pharmacist.R

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
 * @param placeholderImage the placeholder image
 */
@Composable
fun MeteredAsyncImage(
    url: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholderImage: Int = R.drawable.author_andre_pascoa,
) {
    val context = LocalContext.current
    val isOnMeteredConnection = remember {
        isOnMeteredConnection(context = context)
    }

    return if (isOnMeteredConnection)
        Image(
            painter = painterResource(id = placeholderImage),
            contentDescription = contentDescription,
            modifier = modifier
        )
    else
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier
        )
}


