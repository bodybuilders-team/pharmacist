package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.getMedicines

import pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.MedicineModel
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesOutputDto

data class GetMedicinesOutputModel(
    val totalCount: Int,
    val medicines: List<MedicineModel>
) {
    constructor(getMedicinesOutputDto: GetMedicinesOutputDto) : this(
        totalCount = getMedicinesOutputDto.totalCount,
        medicines = getMedicinesOutputDto.medicines.map { MedicineModel(it) }
    )
}
