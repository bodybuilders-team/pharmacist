package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.getPharmacies

import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.PharmacyModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.GetPharmaciesOutputDto

data class GetPharmaciesOutputModel(
    val count: Int,
    val pharmacies: List<PharmacyModel>
) {
    constructor(getPharmaciesOutputDto: GetPharmaciesOutputDto) : this(
        count = getPharmaciesOutputDto.count,
        pharmacies = getPharmaciesOutputDto.pharmacies.map { PharmacyModel(it) }
    )
}