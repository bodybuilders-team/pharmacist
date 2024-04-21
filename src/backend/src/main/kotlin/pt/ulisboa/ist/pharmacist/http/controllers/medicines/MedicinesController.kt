package pt.ulisboa.ist.pharmacist.http.controllers.medicines

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.http.controllers.medicines.createMedicine.CreateMedicineInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.medicines.createMedicine.CreateMedicineOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.medicines.getMedicines.GetMedicineOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.medicines.getMedicines.GetMedicinesWithClosestPharmacyOutputModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.medicines.MedicinesService

/**
 * Controller that handles the requests related to the medicines.
 *
 * @property medicinesService the service that handles the operations related to the medicines
 */
@RestController
@RequestMapping(produces = ["application/json"])
@Authenticated
class MedicinesController(private val medicinesService: MedicinesService) {

    /**
     * Handles the request to get medicines.
     *
     * @return the list of pharmacies
     */
    @GetMapping(Uris.MEDICINES)
    fun getMedicines(
        @RequestParam(Params.SUBSTRING_PARAM) substring: String,
        @RequestParam(Params.LOCATION_PARAM) location: String?,
        @RequestParam(Params.OFFSET_PARAM, defaultValue = Params.OFFSET_DEFAULT.toString()) offset: Int,
        @RequestParam(Params.LIMIT_PARAM, defaultValue = Params.LIMIT_DEFAULT.toString()) limit: Int
    ): GetMedicinesWithClosestPharmacyOutputModel {
        return GetMedicinesWithClosestPharmacyOutputModel(
            medicinesService.getMedicinesWithClosestPharmacy(
                substring = substring,
                location = if (location != null) Location.parse(location)!! else null,
                offset = offset,
                limit = limit
            )
        )
    }


    /**
     * Handles the request to get a medicine by its id.
     *
     * @param medicineId the id of the medicine
     *
     * @return the medicine
     */
    @GetMapping(Uris.MEDICINES_GET_BY_ID)
    fun getMedicineById(
        @PathVariable("mid") medicineId: Long
    ): ResponseEntity<GetMedicineOutputModel> {
        val medicine = medicinesService.getMedicineById(medicineId)
        return ResponseEntity.ok(GetMedicineOutputModel(medicine))
    }

    /**
     * Handles the request to create a medicine.
     *
     * @param inputModel the input model of the request
     * @return information about the added pharmacy
     */
    @PostMapping(Uris.MEDICINES)
    fun createMedicine(
        @Valid @RequestBody inputModel: CreateMedicineInputModel
    ): ResponseEntity<CreateMedicineOutputModel> {
        val createdMedicine = medicinesService.createMedicine(
            name = inputModel.name,
            description = inputModel.description,
            boxPhotoUrl = inputModel.boxPhotoUrl
        )

        return ResponseEntity
            .created(Uris.pharmacyById(createdMedicine.medicineId))
            .body(CreateMedicineOutputModel(createdMedicine))
    }
}