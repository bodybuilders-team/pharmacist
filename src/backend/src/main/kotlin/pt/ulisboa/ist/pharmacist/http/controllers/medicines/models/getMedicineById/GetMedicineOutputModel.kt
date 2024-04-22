package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.getMedicineById

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

/**
 * Output model for the get medicine use case.

 * @property medicineId the medicine id
 * @property name the medicine name
 * @property description the medicine description
 * @property boxPhotoUrl the medicine box photo url
 */
data class GetMedicineOutputModel(
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
