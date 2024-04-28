package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.MedicineDto

data class MedicineStockDto(
    val medicine: MedicineDto,
    val stock: Long
) {
    constructor(medicineStock: MedicineStock) : this(
        medicine = MedicineDto(medicineStock.medicine),
        stock = medicineStock.stock
    )
}