package pt.ulisboa.ist.pharmacist.domain.pharmacies

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidMedicineStockException
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock.Operation.ADD
import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock.Operation.REMOVE
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException

/**
 * A Medicine Stock.
 *
 * @property medicine the medicine
 * @property stock the stock of the medicine
 */
data class MedicineStock(
    val medicine: Medicine,
    private var _stock: Long
) {
    val stock: Long
        get() = _stock

    init {
        if (_stock < 0)
            throw InvalidMedicineStockException("Stock must be a non-negative number.")
    }

    /**
     * Adds a quantity to the stock.
     *
     * @param quantity the quantity to add
     */
    fun add(quantity: Long) {
        _stock += quantity
    }

    /**
     * Removes a quantity from the stock.
     *
     * @param quantity the quantity to remove
     */
    fun remove(quantity: Long) {
        _stock -= quantity
    }

    /**
     * Available operations to perform on the stock.
     *
     * @property ADD the add operation
     * @property REMOVE the remove operation
     */
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
