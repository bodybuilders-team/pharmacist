package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

data class PharmacyModel(
    val id: Long,
    val name: String,
    val location: String,
    val picture: String
) {
    constructor(pharmacyDto: PharmacyDto) : this(
        id = pharmacyDto.id,
        name = pharmacyDto.name,
        location = pharmacyDto.location,
        picture = pharmacyDto.picture
    )
}