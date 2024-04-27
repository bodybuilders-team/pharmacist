package pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacies

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class GetPharmaciesOutputModel(
    val totalCount: Int,
    val pharmacies: List<Pharmacy>
)