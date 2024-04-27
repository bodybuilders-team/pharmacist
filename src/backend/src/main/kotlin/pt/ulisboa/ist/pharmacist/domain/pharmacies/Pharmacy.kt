package pt.ulisboa.ist.pharmacist.domain.pharmacies

/**
 * A Pharmacy.
 *
 * @property pharmacyId the pharmacy id
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the url of the picture of the pharmacy
 * @property creatorId the id of the user that created the pharmacy
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
    val creatorId: Long,
    var globalRatingSum: Double = 0.0,
    var numberOfRatings: Array<Int> = arrayOf(0, 0, 0, 0, 0),
    val medicines: MutableList<MedicineStock> = mutableListOf(),
    var totalFlags: Int = 0,
) {
    val globalRating: Double?
        get() = if (numberOfRatings.sum() == 0) null else globalRatingSum / numberOfRatings.sum()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pharmacy

        return pharmacyId == other.pharmacyId
    }

    override fun hashCode(): Int {
        return pharmacyId.hashCode()
    }
}