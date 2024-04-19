package pt.ulisboa.ist.pharmacist.service.medicines

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesWithClosestPharmacyOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

/**
 * Service that handles the business logic of the medicines.
 *
 * @property medicinesRepository the repository of the medicines
 */
@Service
@Transactional(rollbackFor = [Exception::class])
class MedicinesServiceImpl(
    private val medicinesRepository: MedicinesRepository
) : MedicinesService {

    override fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: String,
        offset: Int,
        limit: Int
    ): GetMedicinesWithClosestPharmacyOutputDto {
        if (offset < 0) throw IllegalArgumentException("Offset must be a positive integer")
        if (limit < 0) throw IllegalArgumentException("Limit must be a positive integer")

        val medicines = medicinesRepository.getMedicinesWithClosestPharmacy(
            substring = substring,
            location = location,
            offset = offset,
            limit = limit
        )
        return GetMedicinesWithClosestPharmacyOutputDto(medicines.size, medicines)
    }

    override fun createMedicine(name: String, description: String, boxPhoto: String): MedicineDto {
        val medicine = medicinesRepository.create(name, description, boxPhoto)
        return MedicineDto(medicine)
    }
}
