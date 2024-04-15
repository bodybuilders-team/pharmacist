package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a refresh token is invalid.
 *
 * @param msg exception message
 */
class InvalidRefreshTokenException(msg: String) : Exception(msg)
