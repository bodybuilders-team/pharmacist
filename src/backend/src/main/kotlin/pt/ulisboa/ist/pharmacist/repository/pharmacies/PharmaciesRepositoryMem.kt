package pt.ulisboa.ist.pharmacist.repository.pharmacies

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

@Repository
class PharmaciesRepositoryMem : PharmaciesRepository {

    private val pharmacies = mutableMapOf<Long, Pharmacy>()

    override fun save(pharmacy: Pharmacy): Pharmacy {
        pharmacies[pharmacy.id ?: pharmacies.size.toLong()] = pharmacy
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
        pharmacies.remove(pharmacy.id)
    }
}