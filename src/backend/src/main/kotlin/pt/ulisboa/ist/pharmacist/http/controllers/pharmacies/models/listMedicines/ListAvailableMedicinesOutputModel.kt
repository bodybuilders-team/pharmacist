package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.listMedicines

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto

/**
 * The output model of the 'List Available Medicines' endpoint.
 *
 * @property medicines the list of medicines
 */
data class ListAvailableMedicinesOutputModel(
    val medicines: List<MedicineStockModel>
) {
    constructor(listAvailableMedicinesOutputDto: ListAvailableMedicinesOutputDto) : this(
        medicines = listAvailableMedicinesOutputDto.medicines.map { MedicineStockModel(it) }
    )
}
