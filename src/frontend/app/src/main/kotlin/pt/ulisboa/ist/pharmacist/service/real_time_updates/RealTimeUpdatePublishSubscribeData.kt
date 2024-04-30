package pt.ulisboa.ist.pharmacist.service.real_time_updates

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateTypes.MEDICINE_NOTIFICATION
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateTypes.PHARMACY
import pt.ulisboa.ist.pharmacist.service.real_time_updates.RealTimeUpdateTypes.PHARMACY_MEDICINE_STOCK

/*
 * Types
 */

object RealTimeUpdateTypes {
    const val PHARMACY = "pharmacy"
    const val PHARMACY_MEDICINE_STOCK = "pharmacy-medicine-stock"
    const val MEDICINE_NOTIFICATION = "medicine-notification"
}

/*
 * Publishes
 */

typealias RealTimeUpdate = RealTimeUpdatePublishingDto

data class RealTimeUpdatePublishingDto(
    val type: String,
    val data: String
)

data class RealTimeUpdatePublishing(
    val type: String,
    val data: RealTimeUpdatePublishingData
) {
    companion object {
        fun pharmacy(pharmacyId: Long, globalRatingSum: Double, numberOfRatings: List<Int>) =
            RealTimeUpdatePublishing(
                PHARMACY, RealTimeUpdatePharmacyPublishingData(
                    pharmacyId = pharmacyId,
                    globalRatingSum = globalRatingSum,
                    numberOfRatings = numberOfRatings
                )
            )

        fun pharmacyMedicineStock(pharmacyId: Long, medicineId: Long, stock: Long) =
            RealTimeUpdatePublishing(
                PHARMACY_MEDICINE_STOCK, RealTimeUpdatePharmacyMedicineStockPublishingData(
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
                MEDICINE_NOTIFICATION, RealTimeUpdateMedicineNotificationPublishingData(
                    pharmacy = pharmacy,
                    medicineStock = medicineStock
                )
            )
    }
}

interface RealTimeUpdatePublishingData

data class RealTimeUpdatePharmacyPublishingData(
    val pharmacyId: Long,
    val globalRatingSum: Double,
    val numberOfRatings: List<Int>
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
