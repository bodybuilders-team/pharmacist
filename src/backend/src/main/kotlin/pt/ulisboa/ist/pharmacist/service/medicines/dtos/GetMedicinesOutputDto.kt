package pt.ulisboa.ist.pharmacist.service.medicines.dtos

data class GetMedicinesOutputDto(
    val totalCount: Int,
    val medicines: List<MedicineDto>
)