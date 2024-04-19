package pt.ulisboa.ist.pharmacist.http.controllers.medicines.getMedicines

import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesWithClosestPharmacyOutputDto

data class GetMedicinesWithClosestPharmacyOutputModel(
    val count: Int,
    val medicines: List<MedicineWithClosestPharmacyOutputModel>
) {
    constructor(getMedicinesWithClosestPharmacyOutputDto: GetMedicinesWithClosestPharmacyOutputDto) : this(
        count = getMedicinesWithClosestPharmacyOutputDto.count,
        medicines = getMedicinesWithClosestPharmacyOutputDto.medicines.map { MedicineWithClosestPharmacyOutputModel(it) }
    )
}
