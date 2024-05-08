package pt.ulisboa.ist.pharmacist.service.real_time_updates

import com.google.gson.Gson
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

/*
 * Types
 */

enum class RTU(
    val type: String,
    val publishDataClass: Class<out RealTimeUpdatePublishingData>,
    val subscribeDataClass: Class<out RealTimeUpdateSubscriptionData>
) {
    NEW_PHARMACY(
        "new-pharmacy",
        RealTimeUpdateNewPharmacyPublishingData::class.java,
        RealTimeUpdateNewPharmaciesSubscriptionData::class.java
    ),
    PHARMACY_USER_RATING(
        "pharmacy-user-rating",
        RealTimeUpdatePharmacyUserRatingPublishingData::class.java,
        RealTimeUpdatePharmacyUserRatingSubscriptionData::class.java
    ),
    PHARMACY_GLOBAL_RATING(
        "pharmacy-global-rating",
        RealTimeUpdatePharmacyGlobalRatingPublishingData::class.java,
        RealTimeUpdatePharmacyGlobalRatingSubscriptionData::class.java
    ),
    PHARMACY_USER_FLAGGED(
        "pharmacy-user-flagged",
        RealTimeUpdatePharmacyUserFlaggedPublishingData::class.java,
        RealTimeUpdatePharmacyUserFlaggedSubscriptionData::class.java
    ),
    PHARMACY_USER_FAVORITED(
        "pharmacy-user-favorited",
        RealTimeUpdatePharmacyUserFavoritedPublishingData::class.java,
        RealTimeUpdatePharmacyUserFavoritedSubscriptionData::class.java
    ),
    PHARMACY_MEDICINE_STOCK(
        "pharmacy-medicine-stock",
        RealTimeUpdatePharmacyMedicineStockPublishingData::class.java,
        RealTimeUpdatePharmacyMedicineStockSubscriptionData::class.java
    ),
    MEDICINE_NOTIFICATION(
        "medicine-notification",
        RealTimeUpdateMedicineNotificationPublishingData::class.java,
        Nothing::class.java //RealTimeUpdateMedicineNotificationSubscriptionData::class.java
    );

    companion object {
        fun getType(type: String): RTU? {
            return entries.find { it.type == type }
        }
    }

    inline fun <reified T : RealTimeUpdatePublishingData> parsePublishJson(data: String): T {
        return Gson().fromJson(
            data,
            publishDataClass
        ) as T
    }

    inline fun <reified T : RealTimeUpdateSubscriptionData> parseSubscribeJson(data: String): T {
        return Gson().fromJson(
            data,
            subscribeDataClass
        ) as T
    }
}

/*
 * Publishes
 */

data class RealTimeUpdatePublishingDto(
    val type: String,
    val data: String
)

data class RealTimeUpdatePublishing(
    val type: RTU,
    val data: RealTimeUpdatePublishingData
) {

    companion object {

        fun newPharmacy(
            pharmacyId: Long,
            name: String,
            location: Location,
            pictureUrl: String
        ) =
            RealTimeUpdatePublishing(
                RTU.NEW_PHARMACY, RealTimeUpdateNewPharmacyPublishingData(
                    pharmacyId = pharmacyId,
                    name = name,
                    location = location,
                    pictureUrl = pictureUrl
                )
            )

        fun pharmacyUserRating(pharmacyId: Long, userId: Long, userRating: Int) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_USER_RATING, RealTimeUpdatePharmacyUserRatingPublishingData(
                    pharmacyId = pharmacyId,
                    userId = userId,
                    userRating = userRating
                )
            )

        fun pharmacyGlobalRating(
            pharmacyId: Long,
            globalRating: Double,
            numberOfRatings: List<Int>
        ) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_GLOBAL_RATING, RealTimeUpdatePharmacyGlobalRatingPublishingData(
                    pharmacyId = pharmacyId,
                    globalRating = globalRating,
                    numberOfRatings = numberOfRatings
                )
            )

        fun pharmacyUserFlagged(pharmacyId: Long, userId: Long, flagged: Boolean) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_USER_FLAGGED, RealTimeUpdatePharmacyUserFlaggedPublishingData(
                    pharmacyId = pharmacyId,
                    userId = userId,
                    flagged = flagged
                )
            )

        fun pharmacyUserFavorited(pharmacyId: Long, userId: Long, favorited: Boolean) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_USER_FAVORITED, RealTimeUpdatePharmacyUserFavoritedPublishingData(
                    pharmacyId = pharmacyId,
                    userId = userId,
                    favorited = favorited
                )
            )

        fun pharmacyMedicineStock(pharmacyId: Long, medicineId: Long, stock: Long) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_MEDICINE_STOCK, RealTimeUpdatePharmacyMedicineStockPublishingData(
                    pharmacyId = pharmacyId,
                    medicineId = medicineId,
                    stock = stock
                )
            )

        fun medicineNotification(
            pharmacy: RealTimeUpdateMedicineNotificationPublishingData.Pharmacy,
            medicineStock: RealTimeUpdateMedicineNotificationPublishingData.MedicineStock
        ) =
            RealTimeUpdatePublishing(
                RTU.MEDICINE_NOTIFICATION, RealTimeUpdateMedicineNotificationPublishingData(
                    pharmacy = pharmacy,
                    medicineStock = medicineStock
                )
            )
    }
}

interface RealTimeUpdatePublishingData

data class RealTimeUpdateNewPharmacyPublishingData(
    var pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyUserRatingPublishingData(
    val pharmacyId: Long,
    val userId: Long,
    val userRating: Int
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyGlobalRatingPublishingData(
    val pharmacyId: Long,
    val globalRating: Double,
    val numberOfRatings: List<Int>
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyUserFlaggedPublishingData(
    var pharmacyId: Long,
    val userId: Long,
    val flagged: Boolean
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyUserFavoritedPublishingData(
    var pharmacyId: Long,
    val userId: Long,
    val favorited: Boolean
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyMedicineStockPublishingData(
    val pharmacyId: Long,
    val medicineId: Long,
    val stock: Long
) : RealTimeUpdatePublishingData

typealias MedicineNotificationData = RealTimeUpdateMedicineNotificationPublishingData

data class RealTimeUpdateMedicineNotificationPublishingData(
    val medicineStock: MedicineStock,
    val pharmacy: Pharmacy
) : RealTimeUpdatePublishingData {
    data class MedicineStock(
        val medicine: Medicine,
        val stock: Long
    )

    data class Pharmacy(
        val pharmacyId: Long,
        val pharmacyName: String
    )
}

/*
 * Subscriptions
 */

data class RealTimeUpdateSubscriptionDto(
    val type: String,
    val data: String
)

data class RealTimeUpdateSubscription(
    val type: RTU,
    val data: RealTimeUpdateSubscriptionData
) {
    fun toDto(): RealTimeUpdateSubscriptionDto {
        return RealTimeUpdateSubscriptionDto(
            type.type,
            Gson().toJson(data)
        )
    }

    companion object {

        fun newPharmacies() =
            RealTimeUpdateSubscription(
                RTU.NEW_PHARMACY, RealTimeUpdateNewPharmaciesSubscriptionData()
            )

        fun pharmacyUserRating(pharmacyId: Long) =
            RealTimeUpdateSubscription(
                RTU.PHARMACY_USER_RATING, RealTimeUpdatePharmacyUserRatingSubscriptionData(
                    pharmacyId = pharmacyId
                )
            )

        fun pharmacyGlobalRating(pharmacyId: Long) =
            RealTimeUpdateSubscription(
                RTU.PHARMACY_GLOBAL_RATING, RealTimeUpdatePharmacyGlobalRatingSubscriptionData(
                    pharmacyId = pharmacyId
                )
            )

        fun pharmacyUserFlagged(pharmacyId: Long) =
            RealTimeUpdateSubscription(
                RTU.PHARMACY_USER_FLAGGED, RealTimeUpdatePharmacyUserFlaggedSubscriptionData(
                    pharmacyId = pharmacyId
                )
            )

        fun pharmacyUserFavorited(pharmacyId: Long) =
            RealTimeUpdateSubscription(
                RTU.PHARMACY_USER_FAVORITED, RealTimeUpdatePharmacyUserFavoritedSubscriptionData(
                    pharmacyId = pharmacyId
                )
            )

        fun pharmacyMedicineStock(pharmacyId: Long, medicineId: Long) =
            RealTimeUpdateSubscription(
                RTU.PHARMACY_MEDICINE_STOCK, RealTimeUpdatePharmacyMedicineStockSubscriptionData(
                    pharmacyId = pharmacyId,
                    medicineId = medicineId
                )
            )

        /*fun medicineNotification(userId: Long, pharmacyId: Long, medicineId: Long) =
            RealTimeUpdateSubscription(
                RTU.MEDICINE_NOTIFICATION, RealTimeUpdateMedicineNotificationSubscriptionData(
                    userId = userId,
                    pharmacyId = pharmacyId,
                    medicineId = medicineId
                )
            )*/
    }
}

interface RealTimeUpdateSubscriptionData

class RealTimeUpdateNewPharmaciesSubscriptionData : RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacyUserRatingSubscriptionData(
    val pharmacyId: Long
) : RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacyGlobalRatingSubscriptionData(
    val pharmacyId: Long
) : RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacyUserFlaggedSubscriptionData(
    val pharmacyId: Long
) : RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacyUserFavoritedSubscriptionData(
    val pharmacyId: Long
) : RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacyMedicineStockSubscriptionData(
    val pharmacyId: Long,
    val medicineId: Long
) : RealTimeUpdateSubscriptionData

/*
data class RealTimeUpdateMedicineNotificationSubscriptionData(
    val userId : Long,
    val pharmacyId : Long,
    val medicineId: Long
): RealTimeUpdateSubscriptionData*/
