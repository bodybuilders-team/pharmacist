package pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.getMedicineById

import pt.ulisboa.ist.pharmacist.http.controllers.medicines.models.MedicineModel
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicineOutputDto

/**
 * Output model for the get medicine use case.

 */
data class GetMedicineOutputModel(
    val medicine: MedicineModel,
    val notificationsActive: Boolean
) {
    constructor(getMedicineOutputDto: GetMedicineOutputDto) : this(
        medicine = MedicineModel(getMedicineOutputDto.medicine),
        notificationsActive = getMedicineOutputDto.notificationsActive
    )
}
