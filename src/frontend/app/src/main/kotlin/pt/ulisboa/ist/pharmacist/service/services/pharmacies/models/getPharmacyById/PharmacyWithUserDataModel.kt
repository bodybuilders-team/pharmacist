package pt.ulisboa.ist.pharmacist.service.services.pharmacies.models.getPharmacyById

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class PharmacyWithUserDataModel(
    val pharmacy: Pharmacy,
    val userRating: PharmacyRatingModel?,
    val userMarkedAsFavorite: Boolean
)

data class PharmacyRatingModel(
    val rating: Float,
    val comment: String
)