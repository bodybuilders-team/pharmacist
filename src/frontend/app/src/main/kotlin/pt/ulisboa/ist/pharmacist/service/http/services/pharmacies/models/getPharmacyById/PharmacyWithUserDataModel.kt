package pt.ulisboa.ist.pharmacist.service.http.services.pharmacies.models.getPharmacyById

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class PharmacyWithUserDataModel(
    val pharmacy: Pharmacy,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean,
    val userFlagged: Boolean
)

