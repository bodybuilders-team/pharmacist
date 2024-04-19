package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addNewMedicine

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.AddNewMedicineOutputDto

/**
 * The output model of the 'Add New Medicine' endpoint.
 *
 * @property newStock the new/current stock of the medicine
 */
data class AddNewMedicineOutputModel(val newStock: Long) {
    constructor(addNewMedicineOutputDto: AddNewMedicineOutputDto) : this(
        newStock = addNewMedicineOutputDto.newStock
    )
}