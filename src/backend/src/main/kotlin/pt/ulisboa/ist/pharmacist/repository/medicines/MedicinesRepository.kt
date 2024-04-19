package pt.ulisboa.ist.pharmacist.repository.medicines

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.MedicineWithClosestPharmacyDto

/**
 * Repository for the [Medicine] entity.
 */
interface MedicinesRepository {

    fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: String,
        offset: Int,
        limit: Int
    ): List<MedicineWithClosestPharmacyDto>

    fun getMedicines(substring: String, offset: Int, limit: Int): List<Medicine>

    fun create(name: String, description: String, boxPhoto: String): Medicine

    fun findByName(name: String): Medicine?

    fun findById(id: Long): Medicine?

    fun findAll(): List<Medicine>

    fun delete(medicine: Medicine)
}
