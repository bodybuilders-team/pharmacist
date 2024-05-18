package pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.listAvailableMedicines

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class ListAvailableMedicinesOutputModel(
    val medicines: List<pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.listAvailableMedicines.MedicineStockModel>
)

data class MedicineStockModel(
    val medicine: Medicine,
    val stock: Long
)