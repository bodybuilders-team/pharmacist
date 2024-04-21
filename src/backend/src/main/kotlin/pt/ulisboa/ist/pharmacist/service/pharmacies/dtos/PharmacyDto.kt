package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class PharmacyDto(
    val pharmacyId: Long,
    val name: String,
    val globalRating: Double,
    val location: LocationDto,
    val pictureUrl: String
) {
    constructor(pharmacy: Pharmacy) : this(
        pharmacyId = pharmacy.pharmacyId,
        name = pharmacy.name,
        globalRating = pharmacy.globalRating,
        location = LocationDto(pharmacy.location),
        pictureUrl = pharmacy.pictureUrl
    )
}