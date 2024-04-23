package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

/**
 * The model of a pharmacy.
 *
 * @property pharmacyId the id of the pharmacy
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the picture URL of the pharmacy
 * @property globalRating the global rating of the pharmacy
 * @property numberOfRatings the number of ratings of the pharmacy for each rating
 */
data class PharmacyModel(
    val pharmacyId: Long,
    val name: String,
    val location: LocationModel,
    val pictureUrl: String,
    val globalRating: Double?,
    val numberOfRatings: Array<Int>
) {
    constructor(pharmacyDto: PharmacyDto) : this(
        pharmacyId = pharmacyDto.pharmacyId,
        name = pharmacyDto.name,
        location = LocationModel(pharmacyDto.location),
        pictureUrl = pharmacyDto.pictureUrl,
        globalRating = pharmacyDto.globalRating,
        numberOfRatings = pharmacyDto.numberOfRatings
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PharmacyModel

        if (pharmacyId != other.pharmacyId) return false
        if (name != other.name) return false
        if (location != other.location) return false
        if (pictureUrl != other.pictureUrl) return false
        if (globalRating != other.globalRating) return false
        if (!numberOfRatings.contentEquals(other.numberOfRatings)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pharmacyId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + pictureUrl.hashCode()
        result = 31 * result + (globalRating?.hashCode() ?: 0)
        result = 31 * result + numberOfRatings.contentHashCode()
        return result
    }
}