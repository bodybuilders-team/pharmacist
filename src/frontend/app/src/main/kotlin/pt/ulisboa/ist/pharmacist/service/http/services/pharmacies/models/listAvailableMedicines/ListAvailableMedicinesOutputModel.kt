package pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.listAvailableMedicines

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class ListAvailableMedicinesOutputModel(
    val medicines: List<MedicineStockModel>
)

data class MedicineStockModel(
    val medicine: Medicine,
    val stock: Long
)