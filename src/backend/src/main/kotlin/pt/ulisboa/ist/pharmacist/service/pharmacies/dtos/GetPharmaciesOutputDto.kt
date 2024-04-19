package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

class GetPharmaciesOutputDto(
    val count: Int,
    val pharmacies: List<PharmacyDto>
) {
    constructor(pharmacies: List<Pharmacy>) : this(
        count = pharmacies.size,
        pharmacies = pharmacies.map { PharmacyDto(it) }
    )
}