package pt.ulisboa.ist.pharmacist.domain.pharmacies

data class Pharmacy(
    val pharmacyId: Long,
    val name: String,
    val location: Location,
    val picture: String,
    val medicines: MutableList<MedicineStock> = mutableListOf()
)