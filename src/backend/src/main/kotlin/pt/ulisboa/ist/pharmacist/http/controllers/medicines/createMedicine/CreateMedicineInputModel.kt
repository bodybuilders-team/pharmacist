package pt.ulisboa.ist.pharmacist.http.controllers.medicines.createMedicine

data class CreateMedicineInputModel(
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)
