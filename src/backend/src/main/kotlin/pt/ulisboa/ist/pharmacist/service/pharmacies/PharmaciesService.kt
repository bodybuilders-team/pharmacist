package pt.ulisboa.ist.pharmacist.service.pharmacies

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.AddNewMedicineOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ChangeMedicineStockOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

/**
 * Service that handles the business logic of the pharmacies.
 */
interface PharmaciesService {

    /**
     * Gets the pharmacies.
     *
     * @param location a location to filter the pharmacies
     * @param range the range to be used in the location filter
     * @param medicine a medicine to filter the pharmacies
     * @param orderBy the field to order the pharmacies
     * @param offset the offset to be used in the pagination
     * @param limit the limit to be used in the pagination
     */
    fun getPharmacies(
        location: String?,
        range: Int?,
        medicine: Long?,
        orderBy: String?,
        offset: Int,
        limit: Int
    ): GetPharmaciesOutputDto

    /**
     * Adds a pharmacy.
     *
     * @param name the name of the pharmacy
     * @param location the location of the pharmacy
     * @param picture the picture of the pharmacy
     */
    fun addPharmacy(name: String, location: String, picture: String): PharmacyDto

    /**
     * Lists the available medicines of a pharmacy.
     *
     * @param pharmacyId the id of the pharmacy
     * @param offset the offset to be used in the pagination
     * @param limit the limit to be used in the pagination
     */
    fun listAvailableMedicines(pharmacyId: Long, offset: Int, limit: Int): ListAvailableMedicinesOutputDto

    /**
     * Adds a new medicine to a pharmacy.
     *
     * @param pharmacyId the id of the pharmacy
     * @param medicineId the id of the medicine
     * @param quantity the quantity of the medicine to add immediately
     */
    fun addNewMedicine(pharmacyId: Long, medicineId: Long, quantity: Long): AddNewMedicineOutputDto

    /**
     * Changes the stock of a medicine in a pharmacy.
     *
     * @param pharmacyId the id of the pharmacy
     * @param medicineId the id of the medicine
     * @param operation the operation to be performed (add or remove)
     * @param quantity the quantity of the medicine to add or remove
     */
    fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: String,
        quantity: Long
    ): ChangeMedicineStockOutputDto
}
