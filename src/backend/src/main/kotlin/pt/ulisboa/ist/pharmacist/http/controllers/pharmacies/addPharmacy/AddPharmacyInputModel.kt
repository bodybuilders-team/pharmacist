package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

/**
 * An Add Pharmacy Input Model.
 *
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the pictureUrl of the pharmacy
 */
data class AddPharmacyInputModel(
    val name: String,
    val location: Location,
    val pictureUrl: String
)
