package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.pharmacies.PharmaciesService
import pt.ulisboa.ist.pharmacist.utils.JwtProvider

/**
 * Controller that handles the requests related to the pharmacies.
 *
 * @property usersService the service that handles the business logic related to the pharmacies
 */
@RestController
@RequestMapping(produces = ["application/json"])
class PharmaciesController(private val pharmaciesService: PharmaciesService) {

    // TODO: Implement the method that handles the request to get the pharmacies
}