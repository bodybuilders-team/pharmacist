package pt.ulisboa.ist.pharmacist.service.pharmacies

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepository
import pt.ulisboa.ist.pharmacist.repository.users.AccessTokensRepository
import pt.ulisboa.ist.pharmacist.service.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.AddNewMedicineOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ChangeMedicineStockOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.ListAvailableMedicinesOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import pt.ulisboa.ist.pharmacist.utils.JwtProvider
import pt.ulisboa.ist.pharmacist.utils.ServerConfiguration

/**
 * Service that handles the business logic of the pharmacies.
 *
 * @property pharmaciesRepository the repository of the pharmacies
 * @property medicinesRepository the repository of the medicines
 */
@Service
@Transactional(rollbackFor = [Exception::class])
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
        val pharmacies = pharmaciesRepository.getPharmacies(location, range, medicine, orderBy, offset, limit)
        return GetPharmaciesOutputDto(pharmacies)
    }

    override fun addPharmacy(name: String, location: String, picture: String): PharmacyDto {
        TODO("Not yet implemented")
    }

    override fun listAvailableMedicines(pharmacyId: Long, offset: Int, limit: Int): ListAvailableMedicinesOutputDto {
        pharmaciesRepository.

        TODO("Not yet implemented")
    }

    override fun addNewMedicine(pharmacyId: Long, medicineId: Long, quantity: Int): AddNewMedicineOutputDto {
        TODO("Not yet implemented")
    }

    override fun changeMedicineStock(
        pharmacyId: Long,
        medicineId: Long,
        operation: String,
        quantity: Int
    ): ChangeMedicineStockOutputDto {
        TODO("Not yet implemented")
    }
}
