package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a medicine is invalid.
 *
 * @param msg exception message
 */
class InvalidMedicineException(msg: String) : Exception(msg)
