package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.listMedicines

import pt.ulisboa.ist.pharmacist.http.controllers.medicines.MedicineModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineStockDto

data class MedicineStockModel(
    val medicine: MedicineModel,
    val stock: Long
) {
    constructor(medicineStockDto: MedicineStockDto) : this(
        medicine = MedicineModel(medicineStockDto.medicine),
        stock = medicineStockDto.stock
    )
}
