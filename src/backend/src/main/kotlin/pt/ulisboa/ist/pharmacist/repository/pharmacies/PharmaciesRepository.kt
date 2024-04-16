package pt.ulisboa.ist.pharmacist.repository.pharmacies

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * Repository for the [Pharmacy] entity.
 */
interface PharmaciesRepository {
    fun save(pharmacy: Pharmacy): Pharmacy

    fun findByName(name: String): Pharmacy?

    fun findById(id: Long): Pharmacy?

    fun findAll(): List<Pharmacy>

    fun delete(pharmacy: Pharmacy)
}
