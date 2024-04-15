package pt.ulisboa.ist.pharmacist.service.exceptions

/**
 * Exception thrown when a refresh token has expired.
 *
 * @param msg exception message
 */
class RefreshTokenExpiredException(msg: String) : Exception(msg)
