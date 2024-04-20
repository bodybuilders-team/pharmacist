package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class PharmacyDto(
    val pharmacyId: Long,
    val name: String,
    val location: String,
    val picture: String
) {
    constructor(pharmacy: Pharmacy) : this(
        pharmacyId = pharmacy.pharmacyId,
        name = pharmacy.name,
        location = pharmacy.location,
        picture = pharmacy.picture
    )
}