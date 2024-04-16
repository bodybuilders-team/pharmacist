package pt.ulisboa.ist.pharmacist.repository.medicines

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

@Repository
class MedicinesRepositoryMem : MedicinesRepository {

    private val medicines = mutableMapOf<Int, Medicine>()

    override fun save(medicine: Medicine): Medicine {
        medicines[medicine.id ?: medicines.size] = medicine
        return medicine
    }

    override fun findByName(name: String): Medicine? {
        return medicines.values.find { it.name == name }
    }

    override fun findById(id: Long): Medicine? {
        return medicines[id.toInt()]
    }

    override fun findAll(): List<Medicine> {
        return medicines.values.toList()
    }

    override fun delete(medicine: Medicine) {
        medicines.remove(medicine.id)
    }
}