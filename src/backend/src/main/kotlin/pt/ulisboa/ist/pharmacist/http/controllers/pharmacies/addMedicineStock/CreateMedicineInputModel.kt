package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addMedicineStock


class CreateMedicineInputModel(
    val name: String,
    val boxPhoto: String,
    val preferredUse: String,
    val quantity: Int,
    val pharmacyId: Long
)