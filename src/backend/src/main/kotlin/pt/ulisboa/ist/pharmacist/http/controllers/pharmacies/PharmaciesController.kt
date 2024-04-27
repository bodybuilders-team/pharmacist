package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.addNewMedicine.AddNewMedicineInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.addNewMedicine.AddNewMedicineOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.addPharmacy.AddPharmacyInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.addPharmacy.AddPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.changeMedicineStock.ChangeMedicineStockInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.changeMedicineStock.ChangeMedicineStockOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.getPharmacies.GetPharmaciesOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.listMedicines.ListAvailableMedicinesOutputModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.pharmacies.PharmaciesService

/**
 * Controller that handles the requests related to the pharmacies.
 *
 * @property pharmaciesService the service that handles the business logic related to the pharmacies
 */
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@Authenticated
class PharmaciesController(private val pharmaciesService: PharmaciesService) {

    /**
     * Handles the request to get pharmacies.
     *
     * @return the list of pharmacies
     */
    @GetMapping(Uris.PHARMACIES)
    fun getPharmacies(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @RequestParam(Params.LOCATION_PARAM, required = false) location: String?,
        @RequestParam(Params.RANGE_PARAM, required = false) range: Int?,
        @RequestParam(Params.MEDICINE_PARAM, required = false) medicine: Long?,
        @RequestParam(Params.ORDER_BY_PARAM, required = false) orderBy: String?,
        @RequestParam(Params.OFFSET_PARAM, defaultValue = Params.OFFSET_DEFAULT.toString()) offset: Int,
        @RequestParam(Params.LIMIT_PARAM, defaultValue = Params.LIMIT_DEFAULT.toString()) limit: Int
    ): GetPharmaciesOutputModel {
        return GetPharmaciesOutputModel(
            pharmaciesService.getPharmacies(
                userId = user.userId,
                location = if (location == null) null else Location.parse(location),
                range = range,
                medicine = medicine,
                orderBy = orderBy,
                offset = offset,
                limit = limit
            )
        )
    }

    /**
     * Handles the request to get a pharmacy by id.
     *
     * @param pid the id of the pharmacy
     *
     * @return information about the pharmacy
     */
    @GetMapping(Uris.PHARMACIES_GET_BY_ID)
    fun getPharmacyById(
        @PathVariable pid: Long,
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User
    ): ResponseEntity<PharmacyWithUserDataModel> {
        return ResponseEntity.ok(PharmacyWithUserDataModel(pharmaciesService.getPharmacyById(user, pid)))
    }

    /**
     * Handles the request to add a pharmacy.
     *
     * @param inputModel the input model of the request
     * @return information about the added pharmacy
     */
    @PostMapping(Uris.PHARMACIES)
    fun addPharmacy(
        @Valid @RequestBody inputModel: AddPharmacyInputModel,
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User
    ): ResponseEntity<AddPharmacyOutputModel> {
        val addedPharmacy = pharmaciesService.addPharmacy(
            name = inputModel.name,
            location = inputModel.location,
            pictureUrl = inputModel.pictureUrl,
            creatorId = user.userId
        )

        return ResponseEntity
            .created(Uris.pharmacyById(addedPharmacy.pharmacyId))
            .body(AddPharmacyOutputModel(addedPharmacy))
    }

    /**
     * Handles the request to list available medicines in a pharmacy.
     *
     * @param pid the id of the pharmacy
     * @param limit the maximum number of medicines to return
     * @param offset the number of medicines to skip
     * @return the list of available medicines
     */
    @GetMapping(Uris.PHARMACY_MEDICINES)
    fun listAvailableMedicines(
        @PathVariable pid: Long,
        @RequestParam(Params.OFFSET_PARAM, defaultValue = Params.OFFSET_DEFAULT.toString()) offset: Int,
        @RequestParam(Params.LIMIT_PARAM, defaultValue = Params.LIMIT_DEFAULT.toString()) limit: Int
    ): ListAvailableMedicinesOutputModel {
        return ListAvailableMedicinesOutputModel(
            pharmaciesService.listAvailableMedicines(
                pharmacyId = pid,
                offset = offset,
                limit = limit
            )
        )
    }

    /**
     * Handles the request to add a new medicine to a pharmacy.
     *
     * @param pid the id of the pharmacy
     * @param mid the id of the medicine
     * @param inputModel the input model of the request
     * @return information about the added medicine
     */
    @PutMapping(Uris.PHARMACY_MEDICINES_GET_BY_ID)
    fun addNewMedicine(
        @PathVariable pid: Long,
        @PathVariable mid: Long,
        @Valid @RequestBody inputModel: AddNewMedicineInputModel
    ): AddNewMedicineOutputModel {
        return AddNewMedicineOutputModel(
            pharmaciesService.addNewMedicine(
                pharmacyId = pid,
                medicineId = mid,
                quantity = inputModel.quantity
            )
        )
    }

    /**
     * Handles the request to rate a pharmacy.
     *
     * @param pid the id of the pharmacy
     * @param pharmacyRating the rating of the pharmacy
     */
    @PostMapping(Uris.PHARMACY_RATINGS)
    fun ratePharmacy(
        @PathVariable pid: String,
        @Valid @RequestBody pharmacyRating: Int, // TODO: Change to a model
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
    ) {
        pharmaciesService.ratePharmacy(
            user,
            pharmacyId = pid.toLong(),
            rating = pharmacyRating
        )
    }

    /**
     * Handles the request to change a medicine stock (add or remove).
     *
     * @param pid the id of the pharmacy
     * @param mid the id of the medicine
     * @param inputModel the input model of the request
     * @return information about the changed medicine stock
     */
    @PatchMapping(Uris.PHARMACY_MEDICINES_GET_BY_ID)
    fun changeMedicineStock(
        @PathVariable pid: Long,
        @PathVariable mid: Long,
        @Valid @RequestBody inputModel: ChangeMedicineStockInputModel
    ): ChangeMedicineStockOutputModel {
        return ChangeMedicineStockOutputModel(
            pharmaciesService.changeMedicineStock(
                pharmacyId = pid,
                medicineId = mid,
                operation = inputModel.operation,
                quantity = inputModel.quantity
            )
        )
    }
}