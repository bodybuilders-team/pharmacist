package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

class ListAvailableMedicinesOutputDto(
    val medicines: List<MedicineStockDto>
)