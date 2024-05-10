package pt.ulisboa.ist.pharmacist.service.medicines.dtos

data class GetMedicinesWithClosestPharmacyOutputDto(
    val medicines: List<MedicineWithClosestPharmacyDto>
)