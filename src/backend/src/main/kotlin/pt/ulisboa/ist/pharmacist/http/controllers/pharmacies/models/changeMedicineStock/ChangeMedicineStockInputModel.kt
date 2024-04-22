package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.changeMedicineStock

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

/**
 * The input model of the 'Change Medicine Stock' endpoint.
 *
 * @property operation the operation to be performed
 * @property quantity the quantity to be added or removed
 */
data class ChangeMedicineStockInputModel(
    val operation: MedicineStock.Operation,
    val quantity: Long
)