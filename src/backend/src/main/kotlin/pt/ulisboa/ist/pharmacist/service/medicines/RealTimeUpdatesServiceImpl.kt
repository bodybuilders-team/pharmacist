package pt.ulisboa.ist.pharmacist.service.medicines

import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateMedicineNotificationPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyMedicineStockPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyMedicineStockSubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacySubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishing
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishingDto
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateSubscriptionDto
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateTypes.MEDICINE_NOTIFICATION
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateTypes.PHARMACY
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateTypes.PHARMACY_MEDICINE_STOCK
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository

/**
 * Service that handles the business logic of real time updates.
 *
 * Uses one flow per user instead of per session, because all sessions of that user need to receive medicine
 * notifications. This is contrary to the other subscriptions, which require a flow per session because the
 * content to be updated is session-specific.
 */
@Service
class RealTimeUpdatesServiceImpl(val usersRepository: UsersRepository) : RealTimeUpdatesService {
    private val userFlows = mutableMapOf<Long, MutableSharedFlow<RealTimeUpdatePublishing>>()
    private val sessionFlows = mutableMapOf<WebSocketSession, MutableSharedFlow<RealTimeUpdatePublishing>>()

    private val pharmacySubscriptions = mutableMapOf<Long, MutableSet<WebSocketSession>>()
    private val pharmacyMedicineStockSubscriptions = mutableMapOf<PharmacyMedicine, MutableSet<WebSocketSession>>()
    // private val medicineNotificationSubscription = mutableMapOf<WebSocketSession, MutableList<RealTimeUpdateSubscriptionData>>()

    data class PharmacyMedicine(
        val pharmacyId: Long,
        val medicineId: Long
    )

    override fun getUserFlow(userId: Long) =
        userFlows.computeIfAbsent(userId) { MutableSharedFlow() }

    override fun getSessionFlow(session: WebSocketSession) =
        sessionFlows.computeIfAbsent(session) { MutableSharedFlow() }

    override fun addSubscription(
        user: User,
        session: WebSocketSession,
        realTimeUpdateSubscriptionDto: RealTimeUpdateSubscriptionDto
    ) {
        when (realTimeUpdateSubscriptionDto.type) {
            PHARMACY -> {
                pharmacySubscriptions.computeIfAbsent(
                    Gson().fromJson(
                        realTimeUpdateSubscriptionDto.data,
                        RealTimeUpdatePharmacySubscriptionData::class.java
                    ).pharmacyId
                ) { mutableSetOf() }
                    .add(session)
            }

            PHARMACY_MEDICINE_STOCK -> {
                val pharmacyMedicineStockSubscriptionData =
                    Gson().fromJson(
                        realTimeUpdateSubscriptionDto.data,
                        RealTimeUpdatePharmacyMedicineStockSubscriptionData::class.java
                    )
                pharmacyMedicineStockSubscriptions.computeIfAbsent(
                    PharmacyMedicine(
                        pharmacyId = pharmacyMedicineStockSubscriptionData.pharmacyId,
                        medicineId = pharmacyMedicineStockSubscriptionData.medicineId
                    )
                ) { mutableSetOf() }
                    .add(session)
            }
            /*MEDICINE_NOTIFICATION -> {
                Gson().fromJson(
                    realTimeUpdateSubscriptionDto.data,
                    RealTimeUpdateMedicineNotificationSubscriptionData::class.java
                )
            }*/
            else -> throw IllegalArgumentException("Invalid subscription type")
        }
    }

    override fun publishUpdate(realTimeUpdatePublishing: RealTimeUpdatePublishing) {
        when (realTimeUpdatePublishing.type) {
            PHARMACY -> {
                val pharmacyUpdate = realTimeUpdatePublishing.data as RealTimeUpdatePharmacyPublishingData
                pharmacySubscriptions[pharmacyUpdate.pharmacyId]?.forEach { session ->
                    sessionFlows[session]?.tryEmit(realTimeUpdatePublishing)
                }
            }

            PHARMACY_MEDICINE_STOCK -> {
                val pharmacyMedicineStockUpdate =
                    realTimeUpdatePublishing.data as RealTimeUpdatePharmacyMedicineStockPublishingData
                pharmacyMedicineStockSubscriptions[
                    PharmacyMedicine(
                        pharmacyId = pharmacyMedicineStockUpdate.pharmacyId,
                        medicineId = pharmacyMedicineStockUpdate.medicineId
                    )
                ]?.forEach { session ->
                    sessionFlows[session]?.tryEmit(realTimeUpdatePublishing)
                }
            }

            MEDICINE_NOTIFICATION -> {
                val medicineNotificationUpdate =
                    realTimeUpdatePublishing.data as RealTimeUpdateMedicineNotificationPublishingData
                usersRepository.findAll().forEach { user ->
                    if (user.favoritePharmacies.any { it.pharmacyId == medicineNotificationUpdate.pharmacy.pharmacyId }
                        && user.medicinesToNotify.any { it.medicineId == medicineNotificationUpdate.medicineStock.medicine.medicineId }) {
                        userFlows[user.userId]?.tryEmit(realTimeUpdatePublishing)
                    }
                }
            }
        }
    }

    override suspend fun sendToSession(
        user: User,
        session: WebSocketSession,
        sendUpdateAction: (RealTimeUpdatePublishingDto) -> Unit
    ) {
        merge(getUserFlow(user.userId), getSessionFlow(session)).collect { realTimeUpdatePublishing ->
            sendUpdateAction(
                RealTimeUpdatePublishingDto(
                    type = realTimeUpdatePublishing.type,
                    data = Gson().toJson(realTimeUpdatePublishing.data)
                )
            )
        }
    }
}