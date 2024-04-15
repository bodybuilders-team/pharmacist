package pt.ulisboa.ist.pharmacist.service.exceptions

/**
 * Exception thrown when a pagination parameter is invalid.
 *
 * @param msg exception message
 */
class InvalidPaginationParamsException(msg: String) : Exception(msg)
