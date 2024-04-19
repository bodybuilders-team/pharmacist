package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

class ListAvailableMedicinesOutputDto(
    val count: Int,
    val medicines: List<MedicineStockDto>
) {
    constructor(medicines: List<MedicineStock>) : this(
        count = medicines.size,
        medicines = medicines.map { MedicineStockDto(it) }
    )
}