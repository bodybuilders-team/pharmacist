package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a given access token is invalid.
 *
 * @param msg exception message
 */
class InvalidAccessTokenException(msg: String) : Exception(msg)
