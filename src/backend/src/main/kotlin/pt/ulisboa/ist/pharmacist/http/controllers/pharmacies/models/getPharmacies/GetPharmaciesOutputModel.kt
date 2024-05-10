package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.getPharmacies

import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto

/**
 * Output model for the GetPharmacies endpoint.

 * @property pharmacies the list of pharmacies
 */
data class GetPharmaciesOutputModel(
    val pharmacies: List<PharmacyWithUserDataModel>
) {
    constructor(getPharmaciesOutputDto: GetPharmaciesOutputDto) : this(
        pharmacies = getPharmaciesOutputDto.pharmacies.map { PharmacyWithUserDataModel(it) }
    )
}