package pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.changeMedicineStock

data class ChangeMedicineStockModel(
    val operation: MedicineStockOperation,
    val quantity: Long
)

enum class MedicineStockOperation {
    ADD,
    REMOVE
}