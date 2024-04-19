package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.changeMedicineStock

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ChangeMedicineStockOutputDto

/**
 * The output model of the 'Change Medicine Stock' endpoint.
 *
 * @property newStock the new/current stock of the medicine
 */
data class ChangeMedicineStockOutputModel(val newStock: Long) {
    constructor(createdMedicineOutputDto: ChangeMedicineStockOutputDto) : this(
        newStock = createdMedicineOutputDto.newStock
    )
}