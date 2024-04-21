package pt.ulisboa.ist.pharmacist.ui.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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


