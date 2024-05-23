package pt.ulisboa.ist.pharmacist.repository.local.medicines

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A medicine.
 *
 * @property medicineId the id of the medicine
 * @property name the name of the medicine
 * @property description the description of the medicine
 * @property boxPhotoUrl the url of the photo of the medicine box
 */
@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String,
    val closestPharmacy: Long?,
    val notificationsActive: Boolean
)


data class PharmacyMedicineFlatEntity(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String,
    val closestPharmacy: Long?,
    val notificationsActive: Boolean,
    val stock: Long?
)