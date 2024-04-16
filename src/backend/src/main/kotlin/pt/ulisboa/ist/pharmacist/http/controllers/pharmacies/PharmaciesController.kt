package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addMedicineStock.CreateMedicineInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addMedicineStock.CreateMedicineOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy.AddPharmacyInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy.AddPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.listMedicines.ListAvailableMedicinesOutputModel
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.pharmacies.PharmaciesService
import java.net.URI

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

    // TODO: Implement the method that handles the request to get the pharmacies

    /**
     * Handles the request to add a pharmacy.
     *
     * @param accessToken the access token of the user
     * @param inputModel the input model of the request
     * @return information about the added pharmacy
     */
    @PostMapping(Uris.PHARMACIES)
    fun addPharmacy(
        @Valid @RequestBody inputModel: AddPharmacyInputModel
    ): ResponseEntity<AddPharmacyOutputModel> {
        val addedPharmacy = pharmaciesService.addPharmacy(inputModel.name, inputModel.location, inputModel.picture)

        return ResponseEntity
            .created(URI.create(Uris.PHARMACIES + "/" + addedPharmacy.id))
            .body(AddPharmacyOutputModel(addedPharmacy))
    }

    /**
     * Handles the request to list available medicines in a pharmacy.
     *
     * @param accessToken the access token of the user
     * @param pid the id of the pharmacy
     * @param limit the maximum number of medicines to return
     * @param offset the number of medicines to skip
     */
    @GetMapping(Uris.PHARMACIES_MEDICINES)
    fun listAvailableMedicines(
        @PathVariable pid: Long,
        @RequestParam(Params.LIMIT_PARAM) limit: Int,
        @RequestParam(Params.OFFSET_PARAM) offset: Int
    ): ListAvailableMedicinesOutputModel {
        return ListAvailableMedicinesOutputModel(
            pharmaciesService.listAvailableMedicines(
                pharmacyId = pid,
                limit,
                offset
            )
        )
    }

    /**
     * Handles the request to create a medicine.
     */
    @PostMapping(Uris.PHARMACIES_MEDICINES)
    fun createMedicine(
        @PathVariable pid: Long,
        @Valid @RequestBody inputModel: CreateMedicineInputModel
    ): CreateMedicineOutputModel {
        val createdMedicineOutputDto = pharmaciesService.createMedicine(
            pharmacyId = pid,
            name = inputModel.name,
            boxPhoto = inputModel.boxPhoto,
            quantity = inputModel.quantity,
            preferredUse = inputModel.preferredUse,
        )

        return CreateMedicineOutputModel(createdMedicineOutputDto)
    }

//    /**
//     * Handles the request to add medicine stock to a pharmacy.
//     */
//    @PostMapping(Uris.PHARMACIES_MEDICINES)
//    fun addMedicineStock(
//        @PathVariable pid: Long,
//        @Valid @RequestBody inputModel: AddMedicineStockInputModel
//    ): AddMedicineStockOutputModel {
//        pharmaciesService.addMedicineStock(pharmacyId = pid, medicineId = inputModel.medicineId, quantity = inputModel.quantity)
//        return AddMedicineStockOutputModel()
//    }
}