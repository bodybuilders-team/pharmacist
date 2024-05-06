package pt.ulisboa.ist.pharmacist.repository.pharmacies

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.pharmacies.PharmacyWithUserData
import pt.ulisboa.ist.pharmacist.repository.MemDataSource
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.utils.paginate

@Repository
class PharmaciesRepositoryMem(private val dataSource: MemDataSource) : PharmaciesRepository {
    private val pharmacies = dataSource.pharmacies

    override fun getPharmacies(
        userId: Long,
        location: Location?,
        range: Int?,
        medicine: Long?,
        orderBy: String?,
        offset: Int,
        limit: Int
    ): List<PharmacyWithUserData> {
        if (offset < 0) throw InvalidArgumentException("Offset must be a positive integer")
        if (limit < 0) throw InvalidArgumentException("Limit must be a positive integer")

        val user = dataSource.users[userId] ?: throw NotFoundException("User with id $userId does not exist")

        return pharmacies.values.toList()
            .let { pharmacies ->
                if (location != null && range != null)
                    pharmacies.filter { pharmacy -> pharmacy.location.distanceTo(location) <= range }
                else
                    pharmacies
            }.let { pharmacies ->
                if (medicine != null)
                    pharmacies.filter { pharmacy -> pharmacy.medicines.any { it.medicine.medicineId == medicine } }
                else
                    pharmacies
            }.let { pharmacies ->
                if (orderBy != null)
                    when (orderBy) {
                        "distance" -> {
                            if (location == null) throw InvalidArgumentException("Location must be provided to order by distance")
                            pharmacies.sortedBy { it.location.distanceTo(location) }
                        }

                        else -> throw InvalidArgumentException("Invalid orderBy field")
                    }
                else
                    pharmacies
            }
            .filter { pharmacy -> !user.flaggedPharmacies.contains(pharmacy.pharmacyId) }
            .filter { pharmacy -> pharmacy.totalFlags < BANNED_PHARMACY_FLAG_THRESHOLD }
            .paginate(limit, offset)
            .map { pharmacy ->
                PharmacyWithUserData(
                    pharmacy,
                    userRating = user.ratings[pharmacy.pharmacyId],
                    userMarkedAsFavorite = user.favoritePharmacies.contains(pharmacy.pharmacyId),
                    userFlagged = user.flaggedPharmacies.contains(pharmacy.pharmacyId)
                )
            }
    }

    override fun listAvailableMedicines(pharmacyId: Long, offset: Int, limit: Int): List<MedicineStock> {
        return pharmacies[pharmacyId]?.medicines
            ?.ifEmpty { null }
            ?.paginate(limit, offset)
            ?: emptyList()
    }

    override fun addNewMedicine(pharmacyId: Long, medicineId: Long, quantity: Long): MedicineStock {
        val pharmacy = pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        val medicine =
            dataSource.medicines[medicineId] ?: throw NotFoundException("Medicine with id $medicineId does not exist")
        pharmacy.medicines.find { it.medicine.medicineId == medicineId }?.let {
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
        val medicineStock = pharmacy.medicines.find { it.medicine.medicineId == medicineId }
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

    override fun create(name: String, location: Location, pictureUrl: String, creatorId: Long): Pharmacy {
        val creator = dataSource.users[creatorId] ?: throw NotFoundException("User with id $creatorId does not exist")
        if (creator.suspended)
            throw InvalidArgumentException("User with id $creatorId is suspended and cannot create a pharmacy")

        val pharmacyId = dataSource.pharmaciesCounter.getAndIncrement()
        val pharmacy = Pharmacy(pharmacyId, name, location, pictureUrl, creatorId)
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
        pharmacies.remove(pharmacy.pharmacyId)
    }

    companion object {
        const val BANNED_PHARMACY_FLAG_THRESHOLD = 5
    }
}