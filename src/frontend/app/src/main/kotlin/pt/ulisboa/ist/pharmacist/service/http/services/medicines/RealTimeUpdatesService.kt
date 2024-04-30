package pt.ulisboa.ist.pharmacist.service.http.services.medicines

import android.util.Log
import com.google.gson.Gson
import java.net.HttpURLConnection
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.http.utils.fromJson
import pt.ulisboa.ist.pharmacist.service.notifications.RealTimeUpdatesBackgroundService.Companion.TAG
import pt.ulisboa.ist.pharmacist.session.SessionManager

class RealTimeUpdatesService(
    val apiEndpoint: String,
    val sessionManager: SessionManager,
    val httpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null


    fun getUpdateFlow(): Flow<RealTimeUpdate> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "WebSocket opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val message = Gson().fromJson<RealTimeUpdate>(text)
                trySend(message)
                Log.d(TAG, "WebSocket message received")
            }


            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "WebSocket closing")
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                if (response != null && response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    sessionManager.clearSession()
                }

                Log.d(TAG, "WebSocket closed from failure")
                close()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "WebSocket closed")
                close()
            }

        }

        val request = Request.Builder()
            .url(apiEndpoint + Uris.UPDATE_SUBSCRIPTIONS)
            .addHeader("Authorization", "Bearer ${sessionManager.accessToken}")
            .build()


        webSocket = httpClient.newWebSocket(request, listener)

        awaitClose {
            webSocket?.close(1000, null)
            Log.d(TAG, "WebSocket closed")
        }
    }
}

object RealTimeUpdateTypes {
    const val PHARMACY = "pharmacy"
    const val PHARMACY_MEDICINE_STOCK = "pharmacy-medicine-stock"
    const val MEDICINE_NOTIFICATION = "medicine-notification"
}

data class RealTimeUpdate(
    val type: String,
    val data: String
)

data class MedicineNotification(
    val medicineStock: MedicineStock,
    val pharmacyId: Long,
)

data class MedicineStock(
    val medicine: Medicine,
    val stock: Long
)

class JsonWebSocket(private val webSocket: WebSocket) {
    fun send(message: Any) {
        val json = Gson().toJson(message)
        webSocket.send(json)
    }

    fun sendByteString(byteString: ByteString) {
        webSocket.send(byteString)
    }
}