package pt.ulisboa.ist.pharmacist.service.medicines.dtos

data class GetMedicinesWithClosestPharmacyOutputDto(
    val totalCount: Int,
    val medicines: List<MedicineWithClosestPharmacyDto>
)