package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.getPharmacies

import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.PharmacyModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto

data class GetPharmaciesOutputModel(
    val totalCount: Int,
    val pharmacies: List<PharmacyModel>
) {
    constructor(getPharmaciesOutputDto: GetPharmaciesOutputDto) : this(
        totalCount = getPharmaciesOutputDto.totalCount,
        pharmacies = getPharmaciesOutputDto.pharmacies.map { PharmacyModel(it) }
    )
}