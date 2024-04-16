package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.addPharmacy

/**
 * An Add Pharmacy Input Model.
 *
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property picture the picture of the pharmacy
 */
data class AddPharmacyInputModel(
    val name: String,
    val location: String,
    val picture: String
)
