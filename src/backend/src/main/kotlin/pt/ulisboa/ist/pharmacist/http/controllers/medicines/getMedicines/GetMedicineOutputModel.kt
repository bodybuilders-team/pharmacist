package pt.ulisboa.ist.pharmacist.http.controllers.medicines.getMedicines

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

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