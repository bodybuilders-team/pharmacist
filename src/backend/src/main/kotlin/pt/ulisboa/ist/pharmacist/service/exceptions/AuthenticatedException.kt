package pt.ulisboa.ist.pharmacist.service.exceptions

/**
 * Exception thrown when an authentication error occurs.
 *
 * @param msg exception message
 */
class AuthenticationException(msg: String) : Exception(msg)