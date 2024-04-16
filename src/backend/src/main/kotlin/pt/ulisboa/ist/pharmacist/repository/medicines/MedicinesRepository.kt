package pt.ulisboa.ist.pharmacist.repository.medicines

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

/**
 * Repository for the [Medicine] entity.
 */
interface MedicinesRepository {

    fun save(medicine: Medicine): Medicine

    fun findByName(name: String): Medicine?

    fun findById(id: Long): Medicine?

    fun findAll(): List<Medicine>

    fun delete(medicine: Medicine)
}
