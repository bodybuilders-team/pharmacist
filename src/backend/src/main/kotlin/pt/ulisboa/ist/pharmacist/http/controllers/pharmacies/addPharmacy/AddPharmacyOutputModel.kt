package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

/**
 * The output model of the 'Add Pharmacy' endpoint.
 *
 * @property pharmacyId the id of the pharmacy
 */
data class AddPharmacyOutputModel(
    val pharmacyId: Long
) {
    constructor(pharmacy: PharmacyDto) : this(
        pharmacyId = pharmacy.pharmacyId
    )
}
