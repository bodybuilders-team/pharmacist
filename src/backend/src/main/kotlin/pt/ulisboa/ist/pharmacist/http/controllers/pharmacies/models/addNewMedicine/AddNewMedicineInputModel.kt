package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.addNewMedicine

/**
 * The input model of the 'Add New Medicine' endpoint.
 *
 * @property quantity the quantity of the medicine
 */
data class AddNewMedicineInputModel(
    val quantity: Long
)