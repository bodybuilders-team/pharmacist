package pt.ulisboa.ist.pharmacist.service.http.services.medicines.models.getMedicinesWithClosestPharmacy

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class GetMedicinesWithClosestPharmacyOutputModel(
    val totalCount: Int,
    val medicines: List<MedicineWithClosestPharmacyOutputModel>
)

data class MedicineWithClosestPharmacyOutputModel(
    val medicine: Medicine,
    val closestPharmacy: Pharmacy?
)