package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyDto

/**
 * An Add Pharmacy Output Model.
 *
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property picture the picture of the pharmacy
 */
data class AddPharmacyOutputModel(
    val name: String,
    val location: String,
    val picture: String
) {
    constructor(pharmacy: PharmacyDto) : this(
        name = pharmacy.name,
        location = pharmacy.location,
        picture = pharmacy.picture
    )
}
