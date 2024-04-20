package pt.ulisboa.ist.pharmacist.repository.pharmacies

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.MemDataSource
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException

@Repository
class PharmaciesRepositoryMem(private val dataSource: MemDataSource) : PharmaciesRepository {
    private val pharmacies = dataSource.pharmacies

    override fun getPharmacies(
        location: String?,
        range: Int?,
        medicine: Long?,
        orderBy: String?,
        offset: Int,
        limit: Int
    ): List<Pharmacy> {
        // TODO implement filtering

        return pharmacies.values.toList()
    }

    override fun listAvailableMedicines(pharmacyId: Long, offset: Int, limit: Int): List<MedicineStock> {
        return pharmacies[pharmacyId]?.medicines
            ?.ifEmpty { null }
            ?.let { it.subList(offset.coerceAtLeast(0), (offset + limit).coerceAtMost(it.size)) }
            ?: emptyList()
    }

    override fun addNewMedicine(pharmacyId: Long, medicineId: Long, quantity: Long): MedicineStock {
        val pharmacy = pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        val medicine =
            dataSource.medicines[medicineId] ?: throw NotFoundException("Medicine with id $medicineId does not exist")
        pharmacy.medicines.find { it.medicine.id == medicineId }?.let {
            throw AlreadyExistsException("Medicine with id $medicineId already exists in pharmacy with id $pharmacyId")
        }

        val medicineStock = MedicineStock(medicine, quantity)
        pharmacy.medicines.add(medicineStock)
        return medicineStock
    }

    override fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: MedicineStock.Operation,
        quantity: Long
    ): MedicineStock {
        val pharmacy = pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        dataSource.medicines[medicineId] ?: throw NotFoundException("Medicine with id $medicineId does not exist")
        val medicineStock = pharmacy.medicines.find { it.medicine.id == medicineId }
            ?: throw NotFoundException("Medicine with id $medicineId does not exist in pharmacy with id $pharmacyId")

        when (operation) {
            MedicineStock.Operation.ADD -> medicineStock.add(quantity)
            MedicineStock.Operation.REMOVE -> {
                if (medicineStock.stock < quantity) {
                    throw InvalidArgumentException("Cannot remove more medicine than available")
                }
                medicineStock.remove(quantity)
            }
        }

        return medicineStock
    }

    override fun create(name: String, location: String, picture: String): Pharmacy {
        val pharmacyId = dataSource.pharmaciesCounter.getAndIncrement()
        val pharmacy = Pharmacy(pharmacyId, name, location, picture, mutableListOf())
        pharmacies[pharmacyId] = pharmacy
        return pharmacy
    }

    override fun findByName(name: String): Pharmacy? {
        return pharmacies.values.find { it.name == name }
    }

    override fun findById(id: Long): Pharmacy? {
        return pharmacies[id]
    }

    override fun findAll(): List<Pharmacy> {
        return pharmacies.values.toList()
    }

    override fun delete(pharmacy: Pharmacy) {
        pharmacies.remove(pharmacy.id)
    }
}