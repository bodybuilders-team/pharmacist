package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.listMedicines

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

/**
 * The output model of the 'List Available Medicines' endpoint.
 *
 * @property count the number of medicines
 * @property medicines the list of medicines
 */
data class ListAvailableMedicinesOutputModel(
    val count: Int,
    val medicines: List<MedicineDto>
) {
    constructor(listAvailableMedicinesOutputDto: ListAvailableMedicinesOutputDto) : this(
        count = listAvailableMedicinesOutputDto.count,
        medicines = listAvailableMedicinesOutputDto.medicines.map { it.medicine }
    )
}
