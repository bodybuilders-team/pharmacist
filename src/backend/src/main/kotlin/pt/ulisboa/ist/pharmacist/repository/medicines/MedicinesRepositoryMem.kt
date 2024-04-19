package pt.ulisboa.ist.pharmacist.repository.medicines

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.repository.MemDataSource

@Repository
class MedicinesRepositoryMem(dataSource: MemDataSource) : MedicinesRepository {

    private val medicines = dataSource.medicines

    override fun save(medicine: Medicine): Medicine {
        medicines[medicine.id] = medicine
        return medicine
    }

    override fun findByName(name: String): Medicine? {
        return medicines.values.find { it.name == name }
    }

    override fun findById(id: Long): Medicine? {
        return medicines[id]
    }

    override fun findAll(): List<Medicine> {
        return medicines.values.toList()
    }

    override fun delete(medicine: Medicine) {
        medicines.remove(medicine.id)
    }
}