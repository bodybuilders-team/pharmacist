package pt.ulisboa.ist.pharmacist.http.controllers.medicines.createMedicine

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

data class CreateMedicineOutputModel(
    val medicineId: Long
) {
    constructor(medicineDto: MedicineDto) : this(
        medicineId = medicineDto.medicineId
    )
}