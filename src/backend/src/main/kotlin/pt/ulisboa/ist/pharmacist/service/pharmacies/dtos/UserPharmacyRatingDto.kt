package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.users.UserPharmacyRating

data class UserPharmacyRatingDto(
    val rating: Float,
    val comment: String
) {
    constructor(rating: UserPharmacyRating) : this(rating.rating, rating.comment)
}
