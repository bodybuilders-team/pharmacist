package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.getMedicines

import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesWithClosestPharmacyOutputDto

data class GetMedicinesWithClosestPharmacyOutputModel(
    val medicines: List<MedicineWithClosestPharmacyOutputModel>
) {
    constructor(getMedicinesWithClosestPharmacyOutputDto: GetMedicinesWithClosestPharmacyOutputDto) : this(
        medicines = getMedicinesWithClosestPharmacyOutputDto.medicines.map { MedicineWithClosestPharmacyOutputModel(it) }
    )
}
