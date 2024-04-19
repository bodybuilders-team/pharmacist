package pt.ulisboa.ist.pharmacist.http.controllers.medicines.getMedicines

import pt.ulisboa.ist.pharmacist.http.controllers.medicines.MedicineModel
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesOutputDto

data class GetMedicinesOutputModel(
    val count: Int,
    val medicines: List<MedicineModel>
) {
    constructor(getMedicinesOutputDto: GetMedicinesOutputDto) : this(
        count = getMedicinesOutputDto.count,
        medicines = getMedicinesOutputDto.medicines.map { MedicineModel(it) }
    )
}
