package pt.ulisboa.ist.pharmacist.service.services.medicines

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
import pt.ulisboa.ist.pharmacist.service.utils.fromJson
import pt.ulisboa.ist.pharmacist.session.SessionManager

class MedicineNotificationService(
    val apiEndpoint: String,
    val sessionManager: SessionManager,
    val httpClient: OkHttpClient
) {


    inline fun <reified T> getUpdateFlow(): Flow<T> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val message = Gson().fromJson<T>(text)
                trySend(message)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                if (response != null && response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    sessionManager.clearSession()
                    close()
                }
            }
        }

        val request = Request.Builder()
            .url("$apiEndpoint/medicines-notifications")
            .addHeader("Authorization", "Bearer ${sessionManager.accessToken}")
            .build()

        val webSocket = httpClient.newWebSocket(request, listener)

        awaitClose {
            webSocket.close(1000, null)
        }
    }
}

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