package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.PharmacyWithUserData

class GetPharmaciesOutputDto(
    val pharmacies: List<PharmacyWithUserDataDto>
)