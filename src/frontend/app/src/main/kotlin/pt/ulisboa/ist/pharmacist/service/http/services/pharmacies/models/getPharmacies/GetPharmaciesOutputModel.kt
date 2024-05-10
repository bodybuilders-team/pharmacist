package pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacies

import pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel

data class GetPharmaciesOutputModel(
    val pharmacies: List<PharmacyWithUserDataModel>
)