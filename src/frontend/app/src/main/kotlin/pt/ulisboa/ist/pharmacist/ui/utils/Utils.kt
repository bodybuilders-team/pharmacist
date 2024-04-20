package pt.ulisboa.ist.pharmacist.ui.utils

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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T> assertNotNull(actual: T) {
    contract { returns() implies (actual != null) }
}

fun isOnMeteredConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.isActiveNetworkMetered
}

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

    return if (isOnMeteredConnection) {
        Image(
            painter = painterResource(id = placeholderImage),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
}