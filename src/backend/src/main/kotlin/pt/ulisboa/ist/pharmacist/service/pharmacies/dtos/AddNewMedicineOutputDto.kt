package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

class AddNewMedicineOutputDto(
    val newStock: Long
) {
    constructor(medicineStockDto: MedicineStockDto) : this(
        newStock = medicineStockDto.stock
    )

    constructor(medicineStock: MedicineStock) : this(
        newStock = medicineStock.stock
    )
}