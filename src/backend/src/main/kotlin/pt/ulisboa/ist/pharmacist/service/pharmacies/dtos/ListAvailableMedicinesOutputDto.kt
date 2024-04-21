package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

class ListAvailableMedicinesOutputDto(
    val totalCount: Int,
    val medicines: List<MedicineStockDto>
) {
    constructor(medicines: List<MedicineStock>) : this(
        totalCount = medicines.size,
        medicines = medicines.map { MedicineStockDto(it) }
    )
}