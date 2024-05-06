package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.getPharmacies

import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models.PharmacyWithUserDataModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto

/**
 * Output model for the GetPharmacies endpoint.

 * @property totalCount the total number of pharmacies
 * @property pharmacies the list of pharmacies
 */
data class GetPharmaciesOutputModel(
    val totalCount: Int,
    val pharmacies: List<PharmacyWithUserDataModel>
) {
    constructor(getPharmaciesOutputDto: GetPharmaciesOutputDto) : this(
        totalCount = getPharmaciesOutputDto.totalCount,
        pharmacies = getPharmaciesOutputDto.pharmacies.map { PharmacyWithUserDataModel(it) }
    )
}