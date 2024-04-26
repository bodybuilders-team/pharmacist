package pt.ulisboa.ist.pharmacist.service.services.medicines

import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.connection.fromJson

class MedicineNotificationService(
    val apiEndpoint: String,
    val httpClient: OkHttpClient
) {

    val request = Request.Builder()
        .url("$apiEndpoint/medicines/notifications")
        .build()

    inline fun <reified T> getUpdateFlow(): Flow<String> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val message = Gson().fromJson<String>(text)
                trySend(message)
            }
        }

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
    private var _stock: Long
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