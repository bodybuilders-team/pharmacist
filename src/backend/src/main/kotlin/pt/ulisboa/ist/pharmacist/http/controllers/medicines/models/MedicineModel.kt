package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

/**
 * A model for a medicine.
 *
 * @property medicineId the medicine's id
 * @property name the medicine's name
 * @property description the medicine's description
 * @property boxPhotoUrl the medicine's box photo url
 */
data class MedicineModel(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
) {
    constructor(medicineDto: MedicineDto) : this(
        medicineId = medicineDto.medicineId,
        name = medicineDto.name,
        description = medicineDto.description,
        boxPhotoUrl = medicineDto.boxPhotoUrl
    )
}
