package pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.getPharmacies

import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.models.getPharmacyById.PharmacyWithUserDataModel

data class GetPharmaciesOutputModel(
    val pharmacies: List<PharmacyWithUserDataModel>
)