package pt.ulisboa.ist.pharmacist.http.pipeline

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import jakarta.servlet.http.HttpServletRequest
import java.net.URI
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import pt.ulisboa.ist.pharmacist.http.media.Problem
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidLoginException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPaginationParamsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPasswordException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException

/**
 * Handles exceptions thrown by the controllers.
 */
@ControllerAdvice
class ExceptionHandler {

    /**
     * Handles Bad Request exceptions.
     *
     * @param ex exception to handle
     * @param request the HTTP request
     * @return response entity with the error message
     */
    @ExceptionHandler(
        value = [
            AlreadyExistsException::class,
            InvalidPasswordException::class,
            InvalidPaginationParamsException::class,
            NotFoundException::class,
            InvalidLoginException::class,
            InvalidArgumentException::class,

            MissingServletRequestParameterException::class,
            HttpRequestMethodNotSupportedException::class,
            HttpMediaTypeNotSupportedException::class,
            HttpMediaTypeNotAcceptableException::class
        ]
    )
    fun handleBadRequest(
        request: HttpServletRequest,
        ex: Exception
    ): ResponseEntity<Any> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Bad Request",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse()

    /**
     * Handles validation exceptions.
     *
     * @param ex exception to handle
     * @param request the HTTP request
     * @return response entity with the error message
     */
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleValidationExceptions(
        request: HttpServletRequest,
        ex: MethodArgumentNotValidException
    ): ResponseEntity<Any> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation Error",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse()

    /**
     * Handles [HttpMessageNotReadableException] exceptions.
     *
     * @param ex exception to handle
     * @param request the HTTP request
     * @return response entity with the error message
     */
    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun handleHttpMessageNotReadableExceptions(
        request: HttpServletRequest,
        ex: HttpMessageNotReadableException
    ): ResponseEntity<Any> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + "invalid-request-body"),
            title = "Invalid request body${
                ex.rootCause.let {
                    ": " +
                            when (it) {
                                is UnrecognizedPropertyException -> "Unknown property '${it.propertyName}'"
                                is JsonParseException -> it.originalMessage
                                is MismatchedInputException -> "Missing property '${it.path.last().fieldName}'"
                                else -> null
                            }
                }
            }",
            status = HttpStatus.BAD_REQUEST.value()
        ).toResponse()

    /**
     * Handles Unauthorized exceptions.
     *
     * @param ex exception to handle
     * @param request the HTTP request
     * @return response entity with the error message
     */
    @ExceptionHandler(value = [AuthenticationException::class])
    fun handleUnauthorized(
        request: HttpServletRequest,
        ex: Exception
    ): ResponseEntity<Any> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + ex.toProblemType()),
            title = ex.message ?: "Unauthorized",
            status = HttpStatus.UNAUTHORIZED.value()
        ).toResponse()

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex exception to handle
     * @param request the HTTP request
     * @return response entity with the error message
     */
    @ExceptionHandler(value = [Exception::class])
    fun handleUncaughtExceptions(
        request: HttpServletRequest,
        ex: Exception
    ): ResponseEntity<Any> =
        Problem(
            type = URI.create(PROBLEMS_DOCS_URI + "internal-server-error"),
            title = "Internal Server Error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        ).toResponse()
            .also { ex.printStackTrace() }

    companion object {
        const val PROBLEMS_DOCS_URI = "https://pharmacist/"

        /**
         * Converts an exception to a problem type name, in kebab-case.
         *
         * @return the problem type name
         */
        fun Exception.toProblemType(): String =
            (this::class.simpleName ?: "Unknown")
                .replace("Exception", "")
                .replace(Regex("([a-z])([A-Z])")) { "${it.groupValues[1]}-${it.groupValues[2]}" }
                .lowercase()
    }
}