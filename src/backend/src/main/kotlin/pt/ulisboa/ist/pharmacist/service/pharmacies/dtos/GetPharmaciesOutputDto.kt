package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.PharmacyWithUserData

class GetPharmaciesOutputDto(
    val totalCount: Int,
    val pharmacies: List<PharmacyWithUserDataDto>
) {
    constructor(pharmacies: List<PharmacyWithUserData>, totalCount: Int) : this(
        totalCount = totalCount,
        pharmacies = pharmacies.map { PharmacyWithUserDataDto(it) }
    )
}