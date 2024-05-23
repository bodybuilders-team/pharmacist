package pt.ulisboa.ist.pharmacist.repository.network.utils

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

/**
 * The URIs of the API.
 */
object Uris {
    const val USERS = "/users"
    const val USERS_LOGIN = "/users/login"
    const val USERS_LOGOUT = "/users/logout"
    const val USERS_UPGRADE = "/users/upgrade"
    const val USERS_GET_BY_ID = "/users/{uid}"
    const val USER_FAVORITE_PHARMACIES = "/users/{uid}/favorite-pharmacies"
    const val USER_FAVORITE_PHARMACIES_GET_BY_ID = "/users/{uid}/favorite-pharmacies/{pid}"
    const val USER_FLAGGED_PHARMACIES = "/users/{uid}/flagged-pharmacies"
    const val USER_FLAGGED_PHARMACIES_GET_BY_ID = "/users/{uid}/flagged-pharmacies/{pid}"
    const val USER_MEDICINE_NOTIFICATIONS = "/users/{uid}/medicine-notifications/{mid}"

    const val PHARMACIES = "/pharmacies"
    const val PHARMACIES_GET_BY_ID = "/pharmacies/{pid}"
    const val PHARMACY_MEDICINES = "/pharmacies/{pid}/medicines"
    const val PHARMACY_MEDICINES_GET_BY_ID = "/pharmacies/{pid}/medicines/{mid}"
    const val PHARMACY_RATINGS = "/pharmacies/{pid}/ratings"
    const val PHARMACY_FLAGS = "/pharmacies/{pid}/flags"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"

    const val UPDATE_SUBSCRIPTIONS = "/update-subscriptions"

    const val CREATE_SIGNED_URL = "/create-signed-url"

    fun medicines(substring: String, location: Location?, limit: Long, offset: Long) =
        "$MEDICINES?substring=$substring" +
                (if (location != null) "&location=$location" else "") +
                ("&limit=$limit&offset=$offset")

    fun pharmacies(
        medicineId: Long?,
        location: Location?,
        orderBy: String?,
        limit: Long?,
        offset: Long?
    ): String =
        "$PHARMACIES?" +
                (if (medicineId != null) "medicine=$medicineId" else "") +
                (if (location != null) "&location=$location" else "") +
                (if (orderBy != null) "&orderBy=$orderBy" else "") +
                (if (limit != null) "&limit=$limit" else "") +
                (if (offset != null) "&offset=$offset" else "")


    fun pharmacyById(pharmacyId: Long) =
        PHARMACIES_GET_BY_ID.replace("{pid}", pharmacyId.toString())

    fun medicineById(medicineId: Long) =
        MEDICINES_GET_BY_ID.replace("{mid}", medicineId.toString())

    fun pharmacyMedicines(pharmacyId: Long, limit: Long?, offset: Long?): String =
        PHARMACY_MEDICINES
            .replace("{pid}", pharmacyId.toString()) + "?" +
                (if (limit != null) "limit=$limit" else "") +
                (if (offset != null) "&offset=$offset" else "")

    fun userFavoritePharmacyById(userId: Long, pharmacyId: Long) =
        USER_FAVORITE_PHARMACIES_GET_BY_ID
            .replace("{uid}", userId.toString())
            .replace("{pid}", pharmacyId.toString())

    fun pharmacyRating(pharmacyId: Long): String =
        PHARMACY_RATINGS.replace("{pid}", pharmacyId.toString())

    fun pharmacyMedicineById(pharmacyId: Long, medicineId: Long): String =
        PHARMACY_MEDICINES_GET_BY_ID
            .replace("{pid}", pharmacyId.toString())
            .replace("{mid}", medicineId.toString())

    fun userFlaggedPharmacyById(userId: Long, pharmacyId: Long) =
        USER_FLAGGED_PHARMACIES_GET_BY_ID
            .replace("{uid}", userId.toString())
            .replace("{pid}", pharmacyId.toString())

    fun medicineNotification(userId: Long, medicineId: Long): String =
        USER_MEDICINE_NOTIFICATIONS
            .replace("{uid}", userId.toString())
            .replace("{mid}", medicineId.toString())
}
