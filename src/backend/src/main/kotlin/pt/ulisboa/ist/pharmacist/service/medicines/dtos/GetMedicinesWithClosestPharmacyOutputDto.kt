package pt.ulisboa.ist.pharmacist.service.medicines.dtos

data class GetMedicinesWithClosestPharmacyOutputDto(
    val count: Int,
    val medicines: List<MedicineWithClosestPharmacyDto>
)