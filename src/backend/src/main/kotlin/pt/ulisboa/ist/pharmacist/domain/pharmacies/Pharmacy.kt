package pt.ulisboa.ist.pharmacist.domain.pharmacies

data class Pharmacy(
    val id: Int,
    val name: String,
    val location: String,
    val picture: String
)