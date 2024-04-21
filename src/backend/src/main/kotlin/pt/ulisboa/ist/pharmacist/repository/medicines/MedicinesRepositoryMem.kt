package pt.ulisboa.ist.pharmacist.repository.medicines

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.repository.MemDataSource
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.MedicineWithClosestPharmacyDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

@Repository
class MedicinesRepositoryMem(private val dataSource: MemDataSource) : MedicinesRepository {

    private val medicines = dataSource.medicines

    override fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: String?,
        offset: Int,
        limit: Int
    ): List<MedicineWithClosestPharmacyDto> {
        val filteredMedicines = medicines.values.filter { it.name.contains(substring) }
            .ifEmpty { null }
            ?.let { it.subList(offset.coerceAtLeast(0), (offset + limit).coerceAtMost(it.size)) }
            ?: emptyList()

        return filteredMedicines.map { medicine ->
            MedicineWithClosestPharmacyDto(
                medicine = MedicineDto(medicine),
                closestPharmacy = dataSource.pharmacies.values.filter { pharmacy ->
                    pharmacy.medicines.map { it.medicine.medicineId }.contains(medicine.medicineId)
                }.minByOrNull { //pharmacy ->
                    // TODO calculate location distances
                    // pharmacy.location.distanceTo(currentLocation)
                    1
                }?.let { closestPharmacy -> PharmacyDto(closestPharmacy) }
            )
        }
    }

    override fun getMedicines(substring: String, offset: Int, limit: Int): List<Medicine> {
        return medicines.values.filter { it.name.contains(substring) }
            .ifEmpty { null }
            ?.let { it.subList(offset.coerceAtLeast(0), (offset + limit).coerceAtMost(it.size)) }
            ?: emptyList()
    }

    override fun create(name: String, description: String, boxPhotoUrl: String): Medicine {
        val medicineId = dataSource.medicinesCounter.getAndIncrement()
        val medicine = Medicine(medicineId, name, description, boxPhotoUrl)
        medicines[medicineId] = medicine
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
        medicines.remove(medicine.medicineId)
    }
}