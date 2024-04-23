package pt.ulisboa.ist.pharmacist.domain.pharmacies

/**
 * A Pharmacy.
 *
 * @property pharmacyId the pharmacy id
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the url of the picture of the pharmacy
 * @property globalRatingSum the sum of all ratings of the pharmacy
 * @property globalRating the global rating of the pharmacy
 * @property numberOfRatings the number of ratings of the pharmacy for each rating
 * @property medicines the medicines of the pharmacy
 * @property totalFlags the total number of flags of the pharmacy
 */
data class Pharmacy(
    val pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    var globalRatingSum: Double = 0.0,
    var numberOfRatings: Array<Int> = arrayOf(0, 0, 0, 0, 0),
    val medicines: MutableList<MedicineStock> = mutableListOf(),
    var totalFlags: Int = 0
) {
    val globalRating: Double?
        get() = if (numberOfRatings.sum() == 0) null else globalRatingSum / numberOfRatings.sum()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pharmacy

        if (pharmacyId != other.pharmacyId) return false
        if (name != other.name) return false
        if (location != other.location) return false
        if (pictureUrl != other.pictureUrl) return false
        if (globalRatingSum != other.globalRatingSum) return false
        if (!numberOfRatings.contentEquals(other.numberOfRatings)) return false
        if (medicines != other.medicines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pharmacyId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + pictureUrl.hashCode()
        result = 31 * result + globalRatingSum.hashCode()
        result = 31 * result + numberOfRatings.contentHashCode()
        result = 31 * result + medicines.hashCode()
        return result
    }
}