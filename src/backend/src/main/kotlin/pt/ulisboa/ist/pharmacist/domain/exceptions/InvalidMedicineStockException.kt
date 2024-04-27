package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a medicine stock is invalid.
 *
 * @param msg exception message
 */
class InvalidMedicineStockException(msg: String) : Exception(msg)
