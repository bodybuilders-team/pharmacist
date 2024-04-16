package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addMedicineStock

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.CreateMedicineOutputDto

class CreateMedicineOutputModel(val medicineId: Long) {
    constructor(createdMedicineOutputDto: CreateMedicineOutputDto) : this(
        medicineId = createdMedicineOutputDto.medicineId
    )
}