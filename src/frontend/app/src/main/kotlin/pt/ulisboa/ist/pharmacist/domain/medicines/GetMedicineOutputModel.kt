package pt.ulisboa.ist.pharmacist.domain.medicines

data class GetMedicineOutputModel(
    val medicine: Medicine,
    val notificationsActive: Boolean
)