package pt.ulisboa.ist.pharmacist.domain.medicines

/**
 * A medicine.
 *
 * @property medicineId the id of the medicine
 * @property name the name of the medicine
 * @property description the description of the medicine
 * @property boxPhotoUrl the url of the photo of the medicine box
 */
data class Medicine(
    var medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)

data class MedicineStock(
    val medicine: Medicine,
    val stock: Long
)

/**
 * A medicine with the closest pharmacy.
 *
 * @property medicineId the id of the medicine
 * @property name the name of the medicine
 * @property description the description of the medicine
 * @property boxPhotoUrl the url of the photo of the medicine box
 */
data class MedicineWithClosestPharmacy(
    var medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String,
    val closestPharmacyId: Long?,
    val closestPharmacyName: String?
)

/**
 * A medicine with the closest pharmacy.
 *
 * @property medicine the medicine
 * @property notificationsActive if the notifications are active for the medicine
 */
data class MedicineWithNotificationStatus(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String,

    val notificationsActive: Boolean
)