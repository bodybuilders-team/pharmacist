package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

class ListAvailableMedicinesOutputDto(
    val count: Int,
    val medicines: List<MedicineDto>
)