package pt.ulisboa.ist.pharmacist.service.pharmacies

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineNotification
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepository
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.medicines.MedicineNotificationService
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.AddNewMedicineOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ChangeMedicineStockOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyWithUserDataDto

/**
 * Service that handles the business logic of the pharmacies.
 *
 * @property pharmaciesRepository the repository of the pharmacies
 * @property medicinesRepository the repository of the medicines
 */
@Service
class PharmaciesServiceImpl(
    private val pharmaciesRepository: PharmaciesRepository,
    private val medicinesRepository: MedicinesRepository,
    private val notificationsService: MedicineNotificationService,
    private val usersRepo: UsersRepository
) : PharmaciesService {

    override fun getPharmacies(
        userId: Long?,
        location: Location?,
        range: Int?,
        medicine: Long?,
        orderBy: String?,
        offset: Int,
        limit: Int
    ): GetPharmaciesOutputDto {
        if (offset < 0) throw InvalidArgumentException("Offset must be a positive integer")
        if (limit < 0) throw InvalidArgumentException("Limit must be a positive integer")

        val pharmacies = pharmaciesRepository.getPharmacies(
            userId = userId,
            location = location,
            range = range,
            medicine = medicine,
            orderBy = orderBy,
            offset = offset,
            limit = limit
        )
        return GetPharmaciesOutputDto(pharmacies, 0)
    }

    override fun addPharmacy(name: String, location: Location, pictureUrl: String): PharmacyDto {
        val pharmacy = pharmaciesRepository.create(name = name, location = location, pictureUrl = pictureUrl)
        return PharmacyDto(pharmacy)
    }

    override fun listAvailableMedicines(pharmacyId: Long, offset: Int, limit: Int): ListAvailableMedicinesOutputDto {
        pharmaciesRepository.findById(pharmacyId)
            ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        if (offset < 0) throw InvalidArgumentException("Offset must be a positive integer")
        if (limit < 0) throw InvalidArgumentException("Limit must be a positive integer")

        val medicines = pharmaciesRepository.listAvailableMedicines(
            pharmacyId = pharmacyId,
            offset = offset,
            limit = limit
        )
        return ListAvailableMedicinesOutputDto(medicines)
    }

    override fun addNewMedicine(pharmacyId: Long, medicineId: Long, quantity: Long): AddNewMedicineOutputDto {
        pharmaciesRepository.findById(pharmacyId)
            ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        medicinesRepository.findById(medicineId)
            ?: throw NotFoundException("Medicine with id $medicineId does not exist")
        if (quantity < 0L) throw InvalidArgumentException("Quantity must be a non-negative number")

        val medicineStock = pharmaciesRepository.addNewMedicine(
            pharmacyId = pharmacyId,
            medicineId = medicineId,
            quantity = quantity
        )
        return AddNewMedicineOutputDto(medicineStock)
    }

    override fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: MedicineStock.Operation,
        quantity: Long
    ): ChangeMedicineStockOutputDto {
        pharmaciesRepository.findById(pharmacyId)
            ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        medicinesRepository.findById(medicineId)
            ?: throw NotFoundException("Medicine with id $medicineId does not exist")
        if (quantity < 0L) throw InvalidArgumentException("Quantity must be a positive integer")

        val medicineStock = pharmaciesRepository.changeMedicineStock(
            pharmacyId = pharmacyId,
            medicineId = medicineId,
            operation = operation,
            quantity = quantity
        )

        notifyMedicineStockChange(pharmacyId, medicineId, medicineStock)

        return ChangeMedicineStockOutputDto(medicineStock)
    }

    private fun notifyMedicineStockChange(pharmacyId: Long, medicineId: Long, medicineStock: MedicineStock) {

        Thread {
            val flows = notificationsService.getFlows()

            runBlocking {
                usersRepo.findAll().forEach { user ->
                    if (user.favoritePharmacies.any { it.pharmacyId == pharmacyId }
                        && user.medicinesToNotify.any { it.medicineId == medicineId }) {
                        if (flows[user.userId] == null)
                            flows[user.userId] = MutableSharedFlow()

                        flows[user.userId]!!.emit(MedicineNotification(medicineStock, pharmacyId))
                    }
                }
            }
        }.start()
    }

    override fun getPharmacyById(user: User, pid: Long): PharmacyWithUserDataDto {
        val pharmacy =
            pharmaciesRepository.findById(pid) ?: throw NotFoundException("Pharmacy with id $pid does not exist")

        return PharmacyWithUserDataDto(
            pharmacy,
            userRating = user.ratings[pid],
            userMarkedAsFavorite = user.favoritePharmacies.contains(pharmacy),
            userFlagged = user.flaggedPharmacies.contains(pharmacy)
        )
    }

    override fun ratePharmacy(user: User, pharmacyId: Long, rating: Int) {
        val pharmacy = pharmaciesRepository.findById(pharmacyId)
            ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")
        if (rating < 0 || rating > 5) throw InvalidArgumentException("Rating must be between 0 and 5")

        val userRating = user.ratings[pharmacyId]

        if (userRating != null) {
            pharmacy.globalRatingSum -= userRating
            pharmacy.numberOfRatings[userRating - 1]--
        }

        user.ratings[pharmacyId] = rating

        pharmacy.globalRatingSum += rating
        pharmacy.numberOfRatings[rating - 1]++
    }
}
