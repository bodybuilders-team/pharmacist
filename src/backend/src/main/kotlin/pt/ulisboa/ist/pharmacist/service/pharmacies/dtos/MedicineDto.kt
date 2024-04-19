package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class MedicineDto(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhoto: String
) {
    constructor(medicine: Medicine) : this(
        medicineId = medicine.id,
        name = medicine.name,
        description = medicine.description,
        boxPhoto = medicine.boxPhoto
    )
}