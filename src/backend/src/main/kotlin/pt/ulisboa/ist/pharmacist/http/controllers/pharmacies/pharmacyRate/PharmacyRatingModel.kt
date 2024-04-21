package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.pharmacyRate

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.UserPharmacyRatingDto

data class PharmacyRatingModel(
    val rating: Int,
    val comment: String
) {
    constructor(rating: UserPharmacyRatingDto) : this(
        rating.rating,
        rating.comment
    )
}
