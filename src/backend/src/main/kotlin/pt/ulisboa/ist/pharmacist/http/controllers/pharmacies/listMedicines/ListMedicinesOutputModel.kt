package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.listMedicines

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

/**
 * An Add Pharmacy Output Model.
 *
 * @property count the number of medicines
 * @property medicines the list of medicines
 */
data class ListAvailableMedicinesOutputModel(
    val count: Int,
    val medicines: List<MedicineDto>
) {
    constructor(listMedicinesOutputDto: ListAvailableMedicinesOutputDto) : this(
        count = listMedicinesOutputDto.count,
        medicines = listMedicinesOutputDto.medicines
    )
}
