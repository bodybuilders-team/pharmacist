package pt.ulisboa.ist.pharmacist.service.exceptions

/**
 * Exception thrown in the login when the password is incorrect for this username.
 *
 * @param msg exception message
 */
class InvalidLoginException(msg: String) : Exception(msg)
