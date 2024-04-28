package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.createMedicine

import pt.ulisboa.ist.pharmacist.service.medicines.dtos.MedicineDto

/**
 * Output model for the create medicine use case.
 *
 * @property medicineId the medicine id
 */
data class CreateMedicineOutputModel(
    val medicineId: Long
) {
    constructor(medicineDto: MedicineDto) : this(
        medicineId = medicineDto.medicineId
    )
}