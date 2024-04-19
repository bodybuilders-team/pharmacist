package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addNewMedicine.AddNewMedicineInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addNewMedicine.AddNewMedicineOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy.AddPharmacyInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy.AddPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.changeMedicineStock.ChangeMedicineStockInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.changeMedicineStock.ChangeMedicineStockOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.getPharmacies.GetPharmaciesOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.listMedicines.ListAvailableMedicinesOutputModel
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.pharmacies.PharmaciesService

/**
 * Controller that handles the requests related to the pharmacies.
 *
 * @property pharmaciesService the service that handles the business logic related to the pharmacies
 */
@RestController
@RequestMapping(produces = ["application/json"])
class PharmaciesController(
    private val pharmaciesService: PharmaciesService
) {
    // TODO: Authentication in endpoints (use @Authenticated)

    // TODO: Implement the method that handles the request to get the pharmacies

    /**
     * Handles the request to get pharmacies.
     *
     * @return the list of pharmacies
     */
    @GetMapping(Uris.PHARMACIES)
    fun getPharmacies(
        @RequestParam(Params.LOCATION_PARAM, required = false) location: String?,
        @RequestParam(Params.RANGE_PARAM, required = false) range: Int?,
        @RequestParam(Params.MEDICINE_PARAM, required = false) medicine: Long?,
        @RequestParam(Params.ORDER_BY_PARAM, required = false) orderBy: String?,
        @RequestParam(Params.OFFSET_PARAM, defaultValue = Params.OFFSET_DEFAULT.toString()) offset: Int,
        @RequestParam(Params.LIMIT_PARAM, defaultValue = Params.LIMIT_DEFAULT.toString()) limit: Int
    ): GetPharmaciesOutputModel {
        val getPharmaciesOutputDto = pharmaciesService.getPharmacies(
            location = location,
            range = range,
            medicine = medicine,
            orderBy = orderBy,
            offset = offset,
            limit = limit
        )

        return GetPharmaciesOutputModel(getPharmaciesOutputDto)
    }

    /**
     * Handles the request to add a pharmacy.
     *
     * @param inputModel the input model of the request
     * @return information about the added pharmacy
     */
    @PostMapping(Uris.PHARMACIES)
    fun addPharmacy(
        @Valid @RequestBody inputModel: AddPharmacyInputModel
    ): ResponseEntity<AddPharmacyOutputModel> {
        val addedPharmacy = pharmaciesService.addPharmacy(
            name = inputModel.name,
            location = inputModel.location,
            picture = inputModel.picture
        )

        return ResponseEntity
            .created(Uris.pharmacyById(addedPharmacy.id))
            .body(AddPharmacyOutputModel(addedPharmacy))
    }

    /**
     * Handles the request to list available medicines in a pharmacy.
     *
     * @param pid the id of the pharmacy
     * @param limit the maximum number of medicines to return
     * @param offset the number of medicines to skip
     */
    @GetMapping(Uris.PHARMACY_MEDICINES)
    fun listAvailableMedicines(
        @PathVariable pid: Long,
        @RequestParam(Params.OFFSET_PARAM) offset: Int,
        @RequestParam(Params.LIMIT_PARAM) limit: Int
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
     * Handles the request to add a medicine.
     */
    @PutMapping(Uris.PHARMACY_MEDICINES_GET_BY_ID)
    fun addNewMedicine(
        @PathVariable pid: Long,
        @PathVariable mid: Long,
        @Valid @RequestBody inputModel: AddNewMedicineInputModel
    ): AddNewMedicineOutputModel {
        val addNewMedicineOutputDto = pharmaciesService.addNewMedicine(
            pharmacyId = pid,
            medicineId = mid,
            quantity = inputModel.quantity
        )

        return AddNewMedicineOutputModel(addNewMedicineOutputDto)
    }

    /**
     * Handles the request to change a medicine stock (add or remove).
     */
    @PatchMapping(Uris.PHARMACY_MEDICINES_GET_BY_ID)
    fun changeMedicineStock(
        @PathVariable pid: Long,
        @PathVariable mid: Long,
        @Valid @RequestBody inputModel: ChangeMedicineStockInputModel
    ): ChangeMedicineStockOutputModel {
        val changeMedicineStockOutputDto = pharmaciesService.changeMedicineStock(
            pharmacyId = pid,
            medicineId = mid,
            operation = inputModel.operation,
            quantity = inputModel.quantity
        )

        return ChangeMedicineStockOutputModel(changeMedicineStockOutputDto)
    }
}