package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

class GetPharmaciesOutputDto(
    val count: Int,
    val pharmacies: List<PharmacyDto>
)