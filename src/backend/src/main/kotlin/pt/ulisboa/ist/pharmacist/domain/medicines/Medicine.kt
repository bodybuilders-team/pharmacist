package pt.ulisboa.ist.pharmacist.domain.medicines

data class Medicine(
    val id: Int,
    val name: String,
    val purpose: String,
    val boxPhoto: String
)
