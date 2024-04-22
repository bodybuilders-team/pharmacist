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
 * @property numberOfRatings the number of ratings of the pharmacy
 * @property medicines the medicines of the pharmacy
 */
data class Pharmacy(
    val pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    var globalRatingSum: Double = 0.0,
    var numberOfRatings: Int = 0,
    val medicines: MutableList<MedicineStock> = mutableListOf()
) {
    val globalRating: Double?
        get() = if (numberOfRatings == 0) null else globalRatingSum / numberOfRatings
}