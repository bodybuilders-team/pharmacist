package pt.ulisboa.ist.pharmacist.domain.pharmacies

data class Pharmacy(
    var id: Long,
    val name: String,
    val location: String,
    val picture: String,
    val medicines: List<MedicineStock>
)