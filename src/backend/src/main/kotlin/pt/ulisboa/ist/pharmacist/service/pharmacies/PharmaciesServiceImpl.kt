package pt.ulisboa.ist.pharmacist.service.pharmacies

import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.controllers.RealTimeUpdatePublishing
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.medicines.RealTimeUpdatesService
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
    private val realTimeUpdatesService: RealTimeUpdatesService
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

    override fun addPharmacy(name: String, location: Location, pictureUrl: String, creatorId: Long): PharmacyDto {
        val pharmacy = pharmaciesRepository.create(name = name, location = location, pictureUrl = pictureUrl, creatorId)
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

        if (medicineStock.stock > 0L)
            notifyMedicineStockChange(pharmacyId, medicineId, medicineStock)

        return AddNewMedicineOutputDto(medicineStock)
    }

    override fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: MedicineStock.Operation,
        quantity: Long
    ): ChangeMedicineStockOutputDto {
        val pharmacy = pharmaciesRepository.findById(pharmacyId)
            ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")

        medicinesRepository.findById(medicineId)
            ?: throw NotFoundException("Medicine with id $medicineId does not exist")

        if (quantity < 0L) throw InvalidArgumentException("Quantity must be a positive integer")

        val prevMedicineStock = pharmacy.medicines.find { it.medicine.medicineId == medicineId }?.stock
            ?: throw NotFoundException("Medicine with id $medicineId does not exist in pharmacy with id $pharmacyId")

        val medicineStock = pharmaciesRepository.changeMedicineStock(
            pharmacyId = pharmacyId,
            medicineId = medicineId,
            operation = operation,
            quantity = quantity
        )

        realTimeUpdatesService.publishUpdate(
            RealTimeUpdatePublishing.pharmacyMedicineStock(
                pharmacyId = pharmacyId,
                medicineId = medicineId,
                stock = medicineStock.stock
            )
        )

        if (prevMedicineStock == 0L && medicineStock.stock > 0L)
            notifyMedicineStockChange(pharmacyId, medicineId, medicineStock)

        return ChangeMedicineStockOutputDto(medicineStock)
    }

    private fun notifyMedicineStockChange(pharmacyId: Long, medicineId: Long, medicineStock: MedicineStock) =
        realTimeUpdatesService.publishUpdate(
            RealTimeUpdatePublishing.medicineNotification(
                pharmacyId = pharmacyId,
                medicineId = medicineId,
                stock = medicineStock.stock
            )
        )


    override fun getPharmacyById(user: User, pharmacyId: Long): PharmacyWithUserDataDto {
        val pharmacy =
            pharmaciesRepository.findById(pharmacyId)
                ?: throw NotFoundException("Pharmacy with id $pharmacyId does not exist")

        return PharmacyWithUserDataDto(
            pharmacy,
            userRating = user.ratings[pharmacyId],
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
            synchronized(pharmacy) {
                pharmacy.globalRatingSum -= userRating
                pharmacy.numberOfRatings[userRating - 1]--
            }
        }

        user.ratings[pharmacyId] = rating

        val newGlobalRatingSum: Double
        val newNumberOfRatings: List<Int>
        synchronized(pharmacy) {
            pharmacy.globalRatingSum += rating
            pharmacy.numberOfRatings[rating - 1]++
            newGlobalRatingSum = pharmacy.globalRatingSum
            newNumberOfRatings = pharmacy.numberOfRatings.toList()
        }
        realTimeUpdatesService.publishUpdate(
            RealTimeUpdatePublishing.pharmacy(
                pharmacyId = pharmacyId,
                globalRatingSum = newGlobalRatingSum,
                numberOfRatings = newNumberOfRatings
            )
        )
    }
}
