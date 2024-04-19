package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addNewMedicine

/**
 * The input model of the 'Add New Medicine' endpoint.
 *
 * @property pharmacyId the id of the pharmacy
 * @property name the name of the medicine
 * @property boxPhoto the photo of the box of the medicine
 * @property description the description of the medicine
 * @property quantity the quantity of the medicine
 */
data class AddNewMedicineInputModel(
    val pharmacyId: Long,
    val name: String,
    val boxPhoto: String,
    val description: String,
    val quantity: Long
)