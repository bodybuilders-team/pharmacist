package pt.ulisboa.ist.pharmacist.http.controllers.medicines

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.service.medicines.MedicinesService

/**
 * Controller that handles the requests related to the medicines.
 *
 * @property usersService the service that handles the business logic related to the medicines
 */
@RestController
@RequestMapping(produces = ["application/json"])
class MedicinesController(private val medicinesService: MedicinesService) {

    // TODO: Implement the method that handles the request to get the pharmacies
}