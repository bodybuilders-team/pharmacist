package pt.ulisboa.ist.pharmacist.service.pharmacies

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.CreateMedicineOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

/**
 * Service that handles the business logic of the pharmacies.
 */
interface PharmaciesService {

    // TODO: Implement the methods of the PharmaciesService interface

    fun addPharmacy(name: String, location: String, picture: String): PharmacyDto

    fun listAvailableMedicines(pharmacyId: Long, limit: Int, offset: Int): ListAvailableMedicinesOutputDto

    fun createMedicine(
        pharmacyId: Long,
        name: String,
        boxPhoto: String,
        quantity: Int,
        preferredUse: String
    ): CreateMedicineOutputDto

    // fun addMedicineStock(pharmacyId: Long, medicineId: Long, quantity: Int): AddMedicineStockOutputDto
}
