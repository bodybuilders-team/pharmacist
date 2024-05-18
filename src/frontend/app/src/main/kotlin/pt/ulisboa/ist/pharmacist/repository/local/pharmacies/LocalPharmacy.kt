package pt.ulisboa.ist.pharmacist.repository.local.pharmacies

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

/**
 * A pharmacy.
 *
 * @property pharmacyId the id of the pharmacy
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the url of the picture of the pharmacy
 */
@Entity(tableName = "pharmacies")
data class LocalPharmacy(
    @PrimaryKey val pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    val globalRating: Double?,
    val numberOfRatings: Array<Int>
)
