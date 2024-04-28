package pt.ulisboa.ist.pharmacist.service.medicines.dtos

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class MedicineDto(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
) {
    constructor(medicine: Medicine) : this(
        medicineId = medicine.medicineId,
        name = medicine.name,
        description = medicine.description,
        boxPhotoUrl = medicine.boxPhotoUrl
    )
}