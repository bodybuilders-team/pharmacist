package pt.ulisboa.ist.pharmacist.repository.pharmacies

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * Repository for the [Pharmacy] entity.
 */
interface PharmaciesRepository {

    fun getPharmacies(
        userId: Long?,
        location: Location?,
        range: Int?,
        medicine: Long?,
        orderBy: String?,
        offset: Int,
        limit: Int
    ): List<Pharmacy>

    fun listAvailableMedicines(pharmacyId: Long, offset: Int, limit: Int): List<MedicineStock>

    fun addNewMedicine(pharmacyId: Long, medicineId: Long, quantity: Long): MedicineStock

    fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: MedicineStock.Operation,
        quantity: Long
    ): MedicineStock

    fun create(name: String, location: Location, pictureUrl: String, creatorId: Long): Pharmacy

    fun findByName(name: String): Pharmacy?

    fun findById(id: Long): Pharmacy?

    fun findAll(): List<Pharmacy>

    fun delete(pharmacy: Pharmacy)
}
