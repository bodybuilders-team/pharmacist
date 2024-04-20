package pt.ulisboa.ist.pharmacist.domain.pharmacies

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException

data class MedicineStock(
    val medicine: Medicine,
    private var _stock: Long
) {
    val stock: Long
        get() = _stock

    fun add(quantity: Long) {
        _stock += quantity
    }

    fun remove(quantity: Long) {
        _stock -= quantity
    }

    enum class Operation {
        ADD,
        REMOVE;

        companion object {
            operator fun invoke(operation: String): Operation = when (operation) {
                "add", "ADD" -> ADD
                "remove", "REMOVE" -> REMOVE
                else -> throw InvalidArgumentException("Invalid operation: $operation")
            }
        }
    }
}
