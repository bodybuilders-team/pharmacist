package pt.ulisboa.ist.pharmacist.repository.local.pharmacies

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
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
data class PharmacyEntity(
    @PrimaryKey val pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    val globalRating: Double?,
    val numberOfRatings: Array<Int>,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean,
    val userFlagged: Boolean
)

class PharmacyConverters {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return "${location.lat},${location.lon}"
    }

    @TypeConverter
    fun toLocation(locationString: String): Location {
        val pieces = locationString.split(",")
        return Location(pieces[0].toDouble(), pieces[1].toDouble())
    }

    @TypeConverter
    fun fromIntArray(array: Array<Int>): String {
        return array.joinToString(separator = ",")
    }

    @TypeConverter
    fun toIntArray(data: String): Array<Int> {
        return data.split(",").map { it.toInt() }.toTypedArray()
    }
}
