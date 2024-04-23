package pt.ulisboa.ist.pharmacist.domain.pharmacies

/**
 * A pharmacy.
 *
 * @property pharmacyId the id of the pharmacy
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the url of the picture of the pharmacy
 */
data class Pharmacy(
    var pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    val globalRating: Double?,
    val numberOfRatings: Array<Int>
)