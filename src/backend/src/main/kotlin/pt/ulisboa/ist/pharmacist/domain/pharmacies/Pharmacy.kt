package pt.ulisboa.ist.pharmacist.domain.pharmacies

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidPharmacyException

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
    init {
        if (pharmacyId < 0)
            throw InvalidPharmacyException("Pharmacy id must be a positive number.")

        if (name.length !in MIN_PHARMACY_NAME_LENGTH..MAX_PHARMACY_NAME_LENGTH)
            throw InvalidPharmacyException(
                "Pharmacy name must be between $MIN_PHARMACY_NAME_LENGTH and $MAX_PHARMACY_NAME_LENGTH characters long."
            )

        if (pictureUrl.isNotEmpty() && !URL_REGEX.toRegex().matches(pictureUrl))
            throw InvalidPharmacyException("Picture URL must be a valid URL.")

        if (creatorId < 0)
            throw InvalidPharmacyException("Creator id must be a positive number.")

        if (globalRatingSum < 0)
            throw InvalidPharmacyException("Global rating sum must be a non-negative number.")

        if (numberOfRatings.any { it < 0 })
            throw InvalidPharmacyException("Number of ratings must be non-negative.")

        if (totalFlags < 0)
            throw InvalidPharmacyException("Total flags must be a non-negative number.")

        if (numberOfRatings.size != 5)
            throw InvalidPharmacyException("Number of ratings must have size 5.")
    }

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

    companion object {
        private const val MIN_PHARMACY_NAME_LENGTH = 3
        private const val MAX_PHARMACY_NAME_LENGTH = 128

        private const val URL_REGEX = "^(http|https)://.*$"
    }
}

data class PharmacyWithUserData(
    val pharmacy: Pharmacy,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean,
    val userFlagged: Boolean
)