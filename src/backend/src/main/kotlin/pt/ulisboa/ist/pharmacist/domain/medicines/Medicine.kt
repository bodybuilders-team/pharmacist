package pt.ulisboa.ist.pharmacist.domain.medicines

data class Medicine(
    var id: Int? = null,
    val name: String,
    val purpose: String,
    val boxPhoto: String
)
