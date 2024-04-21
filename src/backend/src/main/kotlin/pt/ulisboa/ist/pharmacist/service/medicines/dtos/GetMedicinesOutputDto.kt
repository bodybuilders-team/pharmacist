package pt.ulisboa.ist.pharmacist.service.medicines.dtos

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

data class GetMedicinesOutputDto(
    val totalCount: Int,
    val medicines: List<MedicineDto>
)