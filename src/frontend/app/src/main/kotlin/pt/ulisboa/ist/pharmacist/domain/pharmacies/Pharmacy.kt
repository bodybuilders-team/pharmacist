package pt.ulisboa.ist.pharmacist.domain.pharmacies

data class Pharmacy(
    var pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String
)