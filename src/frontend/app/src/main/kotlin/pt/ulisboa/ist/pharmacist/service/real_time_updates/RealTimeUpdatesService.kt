package pt.ulisboa.ist.pharmacist.service.real_time_updates

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import pt.ulisboa.ist.pharmacist.PharmacistApplication
import pt.ulisboa.ist.pharmacist.repository.network.utils.Uris
import pt.ulisboa.ist.pharmacist.repository.network.utils.fromJson
import pt.ulisboa.ist.pharmacist.session.SessionManager
import java.net.HttpURLConnection
import javax.inject.Inject

class RealTimeUpdatesService @Inject constructor(
    val sessionManager: SessionManager,
    val httpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null

    private val updateSubscribeFlow = MutableSharedFlow<Array<RealTimeUpdateSubscription>>()
    private val updatePublishFlow = MutableSharedFlow<RealTimeUpdatePublishingDto>()

    /**
     * Starts the real time updates service, opening the web socket and listening for
     * log in and log out events to determine if a new web socket should open.
     */
    suspend fun startService() {
        while (true) {
            Log.d(TAG, "Real time updates service started")
            if (sessionManager.isLoggedIn()) {
                Log.d(TAG, "Already logged in. Starting WebSocket listener flow")
                getWebSocketListenerFlow().collect(updatePublishFlow::emit)
            }
            Log.d(TAG, "Listening to logInFlow")
            sessionManager.logInFlow.collect { loggedIn ->
                if (loggedIn && sessionManager.isLoggedIn()) {
                    Log.d(TAG, "Became logged in (logInFlow). Starting WebSocket listener flow")
                    getWebSocketListenerFlow().collect(updatePublishFlow::emit)
                }
            }
        }
    }

    private fun getWebSocketListenerFlow(): Flow<RealTimeUpdatePublishingDto> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "WebSocket opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val message = Gson().fromJson<RealTimeUpdatePublishingDto>(text)
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
            .url(PharmacistApplication.API_ENDPOINT + Uris.UPDATE_SUBSCRIPTIONS)
            .addHeader("Authorization", "Bearer ${sessionManager.accessToken}")
            .build()


        webSocket = httpClient.newWebSocket(request, listener)

        launch {
            sessionManager.logInFlow.collect { loggedIn ->
                if (!loggedIn) {
                    Log.d(TAG, "Became logged out (logInFlow). Closing WebSocket listener flow")
                    close()
                }
            }
        }

        launch {
            updateSubscribeFlow.collect { subscriptions ->
                Log.d(TAG, "Sending subscriptions to WebSocket")
                val json = Gson().toJson(subscriptions.map { it.toDto() })
                val sent = webSocket?.send(json)
                Log.d(TAG, "Successfully sent subscriptions to WebSocket: $sent")
            }
        }

        awaitClose {
            webSocket?.close(1000, null)
            Log.d(TAG, "WebSocket channel closed")
        }
    }

    suspend fun subscribeToUpdates(subscriptions: List<RealTimeUpdateSubscription>) {
        subscribeToUpdates(subscriptions.toTypedArray())
    }

    suspend fun subscribeToUpdates(subscriptions: Array<RealTimeUpdateSubscription>) {
        updateSubscribeFlow.emit(subscriptions)
    }


    suspend fun listenForRealTimeUpdates(
        onNewPharmacy: (RealTimeUpdateNewPharmacyPublishingData) -> Unit = {},
        onPharmacyUserRating: (RealTimeUpdatePharmacyUserRatingPublishingData) -> Unit = {},
        onPharmacyGlobalRating: (RealTimeUpdatePharmacyGlobalRatingPublishingData) -> Unit = {},
        onPharmacyUserFlagged: (RealTimeUpdatePharmacyUserFlaggedPublishingData) -> Unit = {},
        onPharmacyUserFavorited: (RealTimeUpdatePharmacyUserFavoritedPublishingData) -> Unit = {},
        onMedicineStock: (RealTimeUpdatePharmacyMedicineStockPublishingData) -> Unit = {},
        onMedicineNotification: (RealTimeUpdateMedicineNotificationPublishingData) -> Unit = {}
    ) {
        updatePublishFlow.collect { realTimeUpdateDto ->
            Log.d(TAG, "Received real time update: ${realTimeUpdateDto.type}")

            when (val realTimeUpdateClass = RTU.getType(realTimeUpdateDto.type)) {
                RTU.NEW_PHARMACY -> onNewPharmacy(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                RTU.PHARMACY_USER_RATING -> onPharmacyUserRating(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                RTU.PHARMACY_GLOBAL_RATING -> onPharmacyGlobalRating(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                RTU.PHARMACY_USER_FLAGGED -> onPharmacyUserFlagged(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                RTU.PHARMACY_USER_FAVORITED -> onPharmacyUserFavorited(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                RTU.PHARMACY_MEDICINE_STOCK -> onMedicineStock(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                RTU.MEDICINE_NOTIFICATION -> onMedicineNotification(
                    realTimeUpdateClass.parsePublishJson(
                        realTimeUpdateDto.data
                    )
                )

                null -> Log.e(
                    "RealTimeUpdatesService",
                    "Unknown real time update type: ${realTimeUpdateDto.type}"
                )
            }
        }
    }

    companion object {
        const val TAG = "RealTimeUpdatesService"
    }
}

