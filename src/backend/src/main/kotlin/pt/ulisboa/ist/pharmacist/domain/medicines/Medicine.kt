package pt.ulisboa.ist.pharmacist.domain.medicines

data class Medicine(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)
