package pt.ulisboa.ist.pharmacist.domain.medicines

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

/**
 * A Medicine Notification.
 *
 * @property medicineStock the medicine stock
 * @property pharmacyId the pharmacy id where the medicine is available
 */
data class MedicineNotification(
    val medicineStock: MedicineStock,
    val pharmacyId: Long,
)