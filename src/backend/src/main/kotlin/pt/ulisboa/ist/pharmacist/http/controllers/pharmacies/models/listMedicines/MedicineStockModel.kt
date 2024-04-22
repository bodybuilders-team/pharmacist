package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.listMedicines

import pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.MedicineModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineStockDto

/**
 * The model of a medicine stock.
 *
 * @property medicine the medicine
 * @property stock the stock
 */
data class MedicineStockModel(
    val medicine: MedicineModel,
    val stock: Long
) {
    constructor(medicineStockDto: MedicineStockDto) : this(
        medicine = MedicineModel(medicineStockDto.medicine),
        stock = medicineStockDto.stock
    )
}
