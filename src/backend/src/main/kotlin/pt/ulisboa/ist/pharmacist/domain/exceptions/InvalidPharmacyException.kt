package pt.ulisboa.ist.pharmacist.domain.exceptions

/**
 * Exception thrown when a pharmacy is invalid.
 *
 * @param msg exception message
 */
class InvalidPharmacyException(msg: String) : Exception(msg)
