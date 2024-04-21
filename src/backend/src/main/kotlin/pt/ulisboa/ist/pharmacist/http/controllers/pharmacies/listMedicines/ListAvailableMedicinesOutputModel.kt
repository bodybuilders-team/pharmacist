package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.listMedicines

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto

/**
 * The output model of the 'List Available Medicines' endpoint.
 *
 * @property totalCount the number of medicines
 * @property medicines the list of medicines
 */
data class ListAvailableMedicinesOutputModel(
    val totalCount: Int,
    val medicines: List<MedicineStockModel>
) {
    constructor(listAvailableMedicinesOutputDto: ListAvailableMedicinesOutputDto) : this(
        totalCount = listAvailableMedicinesOutputDto.totalCount,
        medicines = listAvailableMedicinesOutputDto.medicines.map { MedicineStockModel(it) }
    )
}
