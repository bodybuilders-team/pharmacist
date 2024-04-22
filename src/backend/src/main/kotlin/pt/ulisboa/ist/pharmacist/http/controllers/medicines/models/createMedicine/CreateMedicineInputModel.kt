package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.createMedicine

/**
 * Input model for the create medicine use case.
 *
 * @property name the medicine name
 * @property description the medicine description
 * @property boxPhotoUrl the medicine box photo url
 */
data class CreateMedicineInputModel(
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)
