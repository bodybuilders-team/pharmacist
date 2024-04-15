package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a user is invalid.
 *
 * @param msg exception message
 */
class InvalidUserException(msg: String) : Exception(msg)
