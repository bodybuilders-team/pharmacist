package pt.ulisboa.ist.pharmacist.service.pharmacies

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.AddNewMedicineOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ChangeMedicineStockOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

/**
 * Service that handles the business logic of the pharmacies.
 *
 * @property pharmaciesRepository the repository of the pharmacies
 * @property medicinesRepository the repository of the medicines
 */
@Service
class PharmaciesServiceImpl(
    private val pharmaciesRepository: PharmaciesRepository,
    private val medicinesRepository: MedicinesRepository
) : PharmaciesService {

    override fun getPharmacies(
        location: String?,
        range: Int?,
        medicine: Long?,
        orderBy: String?,
        offset: Int,
        limit: Int
    ): GetPharmaciesOutputDto {
        if (offset < 0) throw InvalidArgumentException("Offset must be a positive integer")
        if (limit < 0) throw InvalidArgumentException("Limit must be a positive integer")

        val pharmacies = pharmaciesRepository.getPharmacies(
            location = location,
            range = range,
            medicine = medicine,
            orderBy = orderBy,
            offset = offset,
            limit = limit
        )
        return GetPharmaciesOutputDto(pharmacies)
    }

    override fun addPharmacy(name: String, location: String, picture: String): PharmacyDto {
        val pharmacy = pharmaciesRepository.create(name = name, location = location, picture = picture)
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
        operation: String,
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
            operation = MedicineStock.Operation(operation),
            quantity = quantity
        )
        return ChangeMedicineStockOutputDto(medicineStock)
    }
}
