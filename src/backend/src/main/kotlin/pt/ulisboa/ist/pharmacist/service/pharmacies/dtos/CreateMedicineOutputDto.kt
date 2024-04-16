package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

class CreateMedicineOutputDto(
    val medicineId: Long,
    val name: String,
    val preferredUse: String
)