package pt.ulisboa.ist.pharmacist.service.exceptions

/**
 * Exception thrown when a resource is not found.
 *
 * @param msg exception message
 */
class NotFoundException(msg: String) : Exception(msg)
