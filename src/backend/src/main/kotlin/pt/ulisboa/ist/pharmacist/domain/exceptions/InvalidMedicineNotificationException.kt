package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a medicine notification is invalid.
 *
 * @param msg exception message
 */
class InvalidMedicineNotificationException(msg: String) : Exception(msg)
