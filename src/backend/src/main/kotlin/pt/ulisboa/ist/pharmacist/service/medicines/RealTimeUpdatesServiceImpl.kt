package pt.ulisboa.ist.pharmacist.service.medicines

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.controllers.RTU
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateMedicineNotificationPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyGlobalRatingPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyGlobalRatingSubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyMedicineStockPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyMedicineStockSubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyUserFavoritedPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyUserFavoritedSubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyUserFlaggedPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyUserFlaggedSubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyUserRatingPublishingData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePharmacyUserRatingSubscriptionData
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishing
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishingDto
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdateSubscriptionDto
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

    private val newPharmacySubscriptions = mutableSetOf<WebSocketSession>()
    private val pharmacyUserRatingSubscriptions = mutableMapOf<UserPharmacy, MutableSet<WebSocketSession>>()
    private val pharmacyGlobalRatingSubscriptions = mutableMapOf<Long, MutableSet<WebSocketSession>>()
    private val pharmacyUserFlaggedSubscriptions = mutableMapOf<UserPharmacy, MutableSet<WebSocketSession>>()
    private val pharmacyUserFavoritedSubscriptions = mutableMapOf<UserPharmacy, MutableSet<WebSocketSession>>()
    private val pharmacyMedicineStockSubscriptions = mutableMapOf<PharmacyMedicine, MutableSet<WebSocketSession>>()
    // private val medicineNotificationSubscriptions = mutableMapOf<WebSocketSession, MutableList<RealTimeUpdateSubscriptionData>>()

    data class PharmacyMedicine(
        val pharmacyId: Long,
        val medicineId: Long
    )

    data class UserPharmacy(
        val userId: Long,
        val pharmacyId: Long
    )

    val publishScope = CoroutineScope(Dispatchers.Default)

    override fun getUserFlow(userId: Long) =
        userFlows.computeIfAbsent(userId) { MutableSharedFlow() }

    override fun getSessionFlow(session: WebSocketSession) =
        sessionFlows.computeIfAbsent(session) { MutableSharedFlow() }

    override fun addSubscription(
        user: User,
        session: WebSocketSession,
        realTimeUpdateSubscriptionDto: RealTimeUpdateSubscriptionDto
    ) {
        when (val realTimeUpdateClass = RTU.getType(realTimeUpdateSubscriptionDto.type)) {
            RTU.NEW_PHARMACY -> {
                newPharmacySubscriptions.add(session)
            }

            RTU.PHARMACY_USER_RATING -> {
                val data: RealTimeUpdatePharmacyUserRatingSubscriptionData =
                    realTimeUpdateClass.parseSubscribeJson(realTimeUpdateSubscriptionDto.data)
                pharmacyUserRatingSubscriptions.computeIfAbsent(
                    UserPharmacy(
                        userId = user.userId,
                        pharmacyId = data.pharmacyId
                    )
                ) { mutableSetOf() }
                    .add(session)
            }

            RTU.PHARMACY_GLOBAL_RATING -> {
                val data: RealTimeUpdatePharmacyGlobalRatingSubscriptionData =
                    realTimeUpdateClass.parseSubscribeJson(realTimeUpdateSubscriptionDto.data)
                pharmacyGlobalRatingSubscriptions.computeIfAbsent(data.pharmacyId) { mutableSetOf() }
                    .add(session)
            }

            RTU.PHARMACY_USER_FLAGGED -> {
                val data: RealTimeUpdatePharmacyUserFlaggedSubscriptionData =
                    realTimeUpdateClass.parseSubscribeJson(realTimeUpdateSubscriptionDto.data)
                pharmacyUserFlaggedSubscriptions.computeIfAbsent(
                    UserPharmacy(
                        userId = user.userId,
                        pharmacyId = data.pharmacyId
                    )
                ) { mutableSetOf() }
                    .add(session)
            }

            RTU.PHARMACY_USER_FAVORITED -> {
                val data: RealTimeUpdatePharmacyUserFavoritedSubscriptionData =
                    realTimeUpdateClass.parseSubscribeJson(realTimeUpdateSubscriptionDto.data)
                pharmacyUserFavoritedSubscriptions.computeIfAbsent(
                    UserPharmacy(
                        userId = user.userId,
                        pharmacyId = data.pharmacyId
                    )
                ) { mutableSetOf() }
                    .add(session)
            }

            RTU.PHARMACY_MEDICINE_STOCK -> {
                val data: RealTimeUpdatePharmacyMedicineStockSubscriptionData =
                    realTimeUpdateClass.parseSubscribeJson(realTimeUpdateSubscriptionDto.data)
                pharmacyMedicineStockSubscriptions.computeIfAbsent(
                    PharmacyMedicine(
                        pharmacyId = data.pharmacyId,
                        medicineId = data.medicineId
                    )
                ) { mutableSetOf() }
                    .add(session)
            }
            /*RTU.MEDICINE_NOTIFICATION -> {
                val data: RealTimeUpdateMedicineNotificationSubscriptionData = realTimeUpdateClass.parseSubscribeJson(realTimeUpdateSubscriptionDto.data)
                medicineNotificationSubscription.computeIfAbsent(session) { mutableListOf() }
                    .add(data)
            }*/
            else -> throw IllegalArgumentException("Invalid subscription type")
        }
    }

    override fun publishUpdate(realTimeUpdatePublishing: RealTimeUpdatePublishing) {
        publishScope.launch {
            when (realTimeUpdatePublishing.type) {
                RTU.NEW_PHARMACY -> {
                    newPharmacySubscriptions.forEach { session ->
                        sessionFlows[session]?.emit(realTimeUpdatePublishing)
                    }
                }

                RTU.PHARMACY_USER_RATING -> {
                    val data = realTimeUpdatePublishing.data as RealTimeUpdatePharmacyUserRatingPublishingData
                    pharmacyUserRatingSubscriptions[UserPharmacy(
                        userId = data.userId,
                        pharmacyId = data.pharmacyId
                    )]?.forEach { session ->
                        sessionFlows[session]?.emit(realTimeUpdatePublishing)
                    }
                }

                RTU.PHARMACY_GLOBAL_RATING -> {
                    val data = realTimeUpdatePublishing.data as RealTimeUpdatePharmacyGlobalRatingPublishingData
                    pharmacyGlobalRatingSubscriptions[data.pharmacyId]?.forEach { session ->
                        sessionFlows[session]?.emit(realTimeUpdatePublishing)
                    }
                }

                RTU.PHARMACY_USER_FLAGGED -> {
                    val data = realTimeUpdatePublishing.data as RealTimeUpdatePharmacyUserFlaggedPublishingData
                    pharmacyUserFlaggedSubscriptions[UserPharmacy(
                        userId = data.userId,
                        pharmacyId = data.pharmacyId
                    )]?.forEach { session ->
                        sessionFlows[session]?.emit(realTimeUpdatePublishing)
                    }
                }

                RTU.PHARMACY_USER_FAVORITED -> {
                    val data = realTimeUpdatePublishing.data as RealTimeUpdatePharmacyUserFavoritedPublishingData
                    pharmacyUserFavoritedSubscriptions[UserPharmacy(
                        userId = data.userId,
                        pharmacyId = data.pharmacyId
                    )]?.forEach { session ->
                        sessionFlows[session]?.emit(realTimeUpdatePublishing)
                    }
                }

                RTU.PHARMACY_MEDICINE_STOCK -> {
                    val pharmacyMedicineStockUpdate =
                        realTimeUpdatePublishing.data as RealTimeUpdatePharmacyMedicineStockPublishingData
                    pharmacyMedicineStockSubscriptions[
                        PharmacyMedicine(
                            pharmacyId = pharmacyMedicineStockUpdate.pharmacyId,
                            medicineId = pharmacyMedicineStockUpdate.medicineId
                        )
                    ]?.forEach { session ->
                        sessionFlows[session]?.emit(realTimeUpdatePublishing)
                    }
                }

                RTU.MEDICINE_NOTIFICATION -> {
                    val medicineNotificationUpdate =
                        realTimeUpdatePublishing.data as RealTimeUpdateMedicineNotificationPublishingData
                    usersRepository.findAll().forEach { user ->
                        if (user.favoritePharmacies.any { it == medicineNotificationUpdate.pharmacy.pharmacyId }
                            && user.medicinesToNotify.any { it == medicineNotificationUpdate.medicineStock.medicine.medicineId }) {
                            userFlows[user.userId]?.emit(realTimeUpdatePublishing)
                        }
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
                    type = realTimeUpdatePublishing.type.type,
                    data = Gson().toJson(realTimeUpdatePublishing.data)
                )
            )
        }
    }
}