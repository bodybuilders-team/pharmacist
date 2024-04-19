package pt.ulisboa.ist.pharmacist.domain.medicines

data class Medicine(
    val id: Long,
    val name: String,
    val description: String,
    val boxPhoto: String
)
