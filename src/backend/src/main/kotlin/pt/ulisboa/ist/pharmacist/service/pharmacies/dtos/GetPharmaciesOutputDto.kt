package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

class GetPharmaciesOutputDto(
    val totalCount: Int,
    val pharmacies: List<PharmacyDto>
) {
    constructor(pharmacies: List<Pharmacy>, totalCount: Int) : this(
        totalCount = totalCount,
        pharmacies = pharmacies.map { PharmacyDto(it) }
    )
}