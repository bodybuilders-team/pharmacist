package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

data class PharmacyModel(
    val pharmacyId: Long,
    val name: String,
    val location: LocationModel,
    val pictureUrl: String
) {
    constructor(pharmacyDto: PharmacyDto) : this(
        pharmacyId = pharmacyDto.pharmacyId,
        name = pharmacyDto.name,
        location = LocationModel(pharmacyDto.location),
        pictureUrl = pharmacyDto.pictureUrl
    )
}