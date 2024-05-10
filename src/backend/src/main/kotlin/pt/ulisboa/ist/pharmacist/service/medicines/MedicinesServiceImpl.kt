package pt.ulisboa.ist.pharmacist.service.medicines

import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicineOutputDto
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesWithClosestPharmacyOutputDto
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.MedicineDto

/**
 * Service that handles the business logic of the medicines.
 *
 * @property medicinesRepository the repository of the medicines
 */
@Service
class MedicinesServiceImpl(
    private val medicinesRepository: MedicinesRepository
) : MedicinesService {

    override fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: Location?,
        offset: Int,
        limit: Int
    ): GetMedicinesWithClosestPharmacyOutputDto {
        if (offset < 0) throw InvalidArgumentException("Offset must be a positive integer")
        if (limit < 0) throw InvalidArgumentException("Limit must be a positive integer")

        val medicines = medicinesRepository.getMedicinesWithClosestPharmacy(
            substring = substring,
            location = location,
            offset = offset,
            limit = limit
        )

        return GetMedicinesWithClosestPharmacyOutputDto(medicines)
    }

    override fun createMedicine(name: String, description: String, boxPhotoUrl: String): MedicineDto {
        val medicine = medicinesRepository.create(name, description, boxPhotoUrl)
        return MedicineDto(medicine)
    }

    override fun getMedicineById(user: User, medicineId: Long): GetMedicineOutputDto {
        val medicine = medicinesRepository.findById(medicineId)
            ?: throw InvalidArgumentException("Medicine with id $medicineId does not exist")

        val medicineNotificationActive = user.medicinesToNotify.contains(medicineId)

        return GetMedicineOutputDto(medicine, medicineNotificationActive)
    }
}
