package pt.ulisboa.ist.pharmacist.service.medicines.dtos

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

data class GetMedicinesOutputDto(
    val count: Int,
    val medicines: List<MedicineDto>
) {
    constructor(medicines: List<Medicine>) : this(
        count = medicines.size,
        medicines = medicines.map { MedicineDto(it) }
    )
}