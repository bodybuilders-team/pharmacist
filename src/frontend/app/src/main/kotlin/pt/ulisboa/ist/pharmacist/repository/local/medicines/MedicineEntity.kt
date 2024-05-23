package pt.ulisboa.ist.pharmacist.repository.local.medicines

import androidx.room.ColumnInfo
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
    @ColumnInfo(defaultValue = "0") val notificationsActive: Boolean
)

data class MedicineWithClosestPharmacyEntity(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String,
    val closestPharmacyId: Long?,
    val closestPharmacyName: String?
)


data class PharmacyMedicineFlatEntity(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String,
    val notificationsActive: Boolean,
    val stock: Long?
)