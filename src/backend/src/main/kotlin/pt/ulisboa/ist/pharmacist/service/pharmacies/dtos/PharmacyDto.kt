package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

data class PharmacyDto(
    val id: Long,
    val name: String,
    val location: String,
    val picture: String
)