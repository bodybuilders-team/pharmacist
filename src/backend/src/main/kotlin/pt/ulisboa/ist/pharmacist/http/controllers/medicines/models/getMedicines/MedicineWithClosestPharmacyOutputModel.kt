package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.getMedicines

import pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.MedicineModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.PharmacyModel
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.MedicineWithClosestPharmacyDto

data class MedicineWithClosestPharmacyOutputModel(
    val medicine: MedicineModel,
    val closestPharmacy: PharmacyModel?
) {
    constructor(medicineWithClosestPharmacyDto: MedicineWithClosestPharmacyDto) : this(
        medicine = MedicineModel(medicineWithClosestPharmacyDto.medicine),
        closestPharmacy = medicineWithClosestPharmacyDto.closestPharmacy?.let { PharmacyModel(it) }
    )
}
