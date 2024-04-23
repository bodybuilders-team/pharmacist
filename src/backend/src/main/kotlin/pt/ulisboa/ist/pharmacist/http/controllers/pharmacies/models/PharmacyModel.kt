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
}