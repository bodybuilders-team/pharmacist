package pt.ulisboa.ist.pharmacist.repository.network.services.medicines.models.getMedicinesWithClosestPharmacy

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class GetMedicinesWithClosestPharmacyOutputModel(
    val medicines: List<MedicineWithClosestPharmacyOutputModel>
)

data class MedicineWithClosestPharmacyOutputModel(
    val medicine: Medicine,
    val closestPharmacy: Pharmacy?
)