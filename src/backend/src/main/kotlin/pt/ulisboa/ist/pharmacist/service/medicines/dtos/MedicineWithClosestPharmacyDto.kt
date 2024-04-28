package pt.ulisboa.ist.pharmacist.service.medicines.dtos

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

data class MedicineWithClosestPharmacyDto(
    val medicine: MedicineDto,
    val closestPharmacy: PharmacyDto?
)