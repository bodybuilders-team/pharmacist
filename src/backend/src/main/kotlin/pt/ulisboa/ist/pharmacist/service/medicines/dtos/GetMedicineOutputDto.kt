package pt.ulisboa.ist.pharmacist.service.medicines.dtos

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class GetMedicineOutputDto(
    val medicine: MedicineDto,
    val notificationsActive: Boolean
) {
    constructor(medicine: Medicine, notificationsActive: Boolean) : this(
        medicine = MedicineDto(medicine),
        notificationsActive = notificationsActive
    )
}