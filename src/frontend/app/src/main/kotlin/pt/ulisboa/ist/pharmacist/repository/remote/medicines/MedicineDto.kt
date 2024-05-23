package pt.ulisboa.ist.pharmacist.repository.remote.medicines

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * A medicine.
 *
 * @property medicineId the id of the medicine
 * @property name the name of the medicine
 * @property description the description of the medicine
 * @property boxPhotoUrl the url of the photo of the medicine box
 */
data class MedicineDto(
    var medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)

data class GetMedicinesWithClosestPharmacyOutputDto(
    val medicines: List<MedicineWithClosestPharmacyOutputDto>
)

data class MedicineWithClosestPharmacyOutputDto(
    val medicine: MedicineDto,
    val closestPharmacy: Pharmacy?
)

data class CreateMedicineInputDto(
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)

data class CreateMedicineOutputDto(
    val medicineId: Long
)