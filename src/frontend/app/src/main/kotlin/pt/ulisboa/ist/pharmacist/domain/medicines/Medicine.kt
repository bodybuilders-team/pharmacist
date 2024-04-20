package pt.ulisboa.ist.pharmacist.domain.medicines

data class Medicine(
    var medicineId: Long,
    val name: String,
    val purpose: String,
    val boxPhotoUrl: String
)
