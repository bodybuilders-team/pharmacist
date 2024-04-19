package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.changeMedicineStock

/**
 * The input model of the 'Change Medicine Stock' endpoint.
 *
 * @property operation the operation to be performed
 * @property quantity the quantity to be added or removed
 */
data class ChangeMedicineStockInputModel(
    val operation: String,
    val quantity: Long
)