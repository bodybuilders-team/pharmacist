package pt.ulisboa.ist.pharmacist.service.exceptions

/**
 * Exception thrown when an argument is invalid.
 *
 * @param msg exception message
 */
class InvalidArgumentException(msg: String) : Exception(msg)
