package pt.ulisboa.ist.pharmacist.domain.medicines

data class Medicine(
    val id: Long,
    val name: String,
    val purpose: String,
    val boxPhoto: String
)
