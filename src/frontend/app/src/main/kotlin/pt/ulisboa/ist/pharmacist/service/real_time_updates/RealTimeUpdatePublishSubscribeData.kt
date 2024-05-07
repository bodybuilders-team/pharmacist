package pt.ulisboa.ist.pharmacist.service.real_time_updates

import com.google.gson.Gson
import org.jetbrains.annotations.Contract
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.http.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.http.connection.isSuccess
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/*
 * Types
 */

enum class RTU(val type: String, val dataClass: Class<out RealTimeUpdatePublishingData>) {
    NEW_PHARMACY(
        "new-pharmacy",
        RealTimeUpdateNewPharmacyPublishingData::class.java
    ),
    PHARMACY_USER_RATING(
        "pharmacy-user-rating",
        RealTimeUpdatePharmacyUserRatingPublishingData::class.java
    ),
    PHARMACY_GLOBAL_RATING(
        "pharmacy-global-rating",
        RealTimeUpdatePharmacyGlobalRatingPublishingData::class.java
    ),
    PHARMACY_USER_FLAGGED(
        "pharmacy-user-flagged",
        RealTimeUpdatePharmacyUserFlaggedPublishingData::class.java
    ),
    PHARMACY_USER_FAVORITED(
        "pharmacy-user-favorited",
        RealTimeUpdatePharmacyUserFavoritedPublishingData::class.java
    ),
    PHARMACY_MEDICINE_STOCK(
        "pharmacy-medicine-stock",
        RealTimeUpdatePharmacyMedicineStockPublishingData::class.java
    ),
    MEDICINE_NOTIFICATION(
        "medicine-notification",
        RealTimeUpdateMedicineNotificationPublishingData::class.java
    );

    companion object {
        fun getType(type: String): RTU? {
            return entries.find { it.type == type }
        }
    }

    inline fun <reified T: RealTimeUpdatePublishingData> parseJson(data: String): T {
        return Gson().fromJson(
            data,
            dataClass
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
    val type: String,
    val data: RealTimeUpdatePublishingData
) {
    constructor(rtu: RTU, data: RealTimeUpdatePublishingData) : this(rtu.type, data)

    companion object {

        fun newPharmacy(
            pharmacyId: Long,
            name: String,
            location: Location,
            pictureUrl: String,
            globalRating: Double?,
            numberOfRatings: List<Int>
        ) =
            RealTimeUpdatePublishing(
                RTU.NEW_PHARMACY, RealTimeUpdateNewPharmacyPublishingData(
                    pharmacyId = pharmacyId,
                    name = name,
                    location = location,
                    pictureUrl = pictureUrl,
                    globalRating = globalRating,
                    numberOfRatings = numberOfRatings
                )
            )

        fun pharmacyUserRating(pharmacyId: Long, userRating: Int) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_USER_RATING, RealTimeUpdatePharmacyUserRatingPublishingData(
                    pharmacyId = pharmacyId,
                    userRating = userRating
                )
            )

        fun pharmacyGlobalRating(
            pharmacyId: Long,
            globalRatingSum: Double,
            numberOfRatings: List<Int>
        ) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_GLOBAL_RATING, RealTimeUpdatePharmacyGlobalRatingPublishingData(
                    pharmacyId = pharmacyId,
                    globalRatingSum = globalRatingSum,
                    numberOfRatings = numberOfRatings
                )
            )

        fun pharmacyUserFlagged(pharmacyId: Long, flagged: Boolean) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_USER_FLAGGED, RealTimeUpdatePharmacyUserFlaggedPublishingData(
                    pharmacyId = pharmacyId,
                    flagged = flagged
                )
            )

        fun pharmacyUserFavorited(pharmacyId: Long, favorited: Boolean) =
            RealTimeUpdatePublishing(
                RTU.PHARMACY_USER_FAVORITED, RealTimeUpdatePharmacyUserFavoritedPublishingData(
                    pharmacyId = pharmacyId,
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
    val pictureUrl: String,
    val globalRating: Double?,
    val numberOfRatings: List<Int>
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyUserRatingPublishingData(
    val pharmacyId: Long,
    val userRating: Int
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyGlobalRatingPublishingData(
    val pharmacyId: Long,
    val globalRatingSum: Double,
    val numberOfRatings: List<Int>
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyUserFlaggedPublishingData(
    var pharmacyId: Long,
    val flagged: Boolean
) : RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyUserFavoritedPublishingData(
    var pharmacyId: Long,
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

interface RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacyMedicineStockSubscriptionData(
    val pharmacyId: Long,
    val medicineId: Long
) : RealTimeUpdateSubscriptionData

data class RealTimeUpdatePharmacySubscriptionData(
    val pharmacyId: Long
) : RealTimeUpdateSubscriptionData

/*
data class RealTimeUpdateMedicineNotificationSubscriptionData(
    val userId : Long,
    val pharmacyId : Long,
    val medicineId: Long
): RealTimeUpdateSubscriptionData*/
