package pt.ulisboa.ist.pharmacist.service.utils

/**
 * The URIs of the API.
 */
object Uris {
    const val USERS = "/users"
    const val USERS_LOGIN = "/users/login"
    const val USERS_LOGOUT = "/users/logout"
    const val USERS_GET_BY_ID = "/users/{uid}"
    const val USER_FAVORITE_PHARMACIES = "/users/{uid}/favorite-pharmacies"
    const val USER_FAVORITE_PHARMACIES_GET_BY_ID = "/users/{uid}/favorite-pharmacies/{pid}"

    const val PHARMACIES = "/pharmacies"
    const val PHARMACIES_GET_BY_ID = "/pharmacies/{pid}"
    const val PHARMACY_MEDICINES = "/pharmacies/{pid}/medicines"
    const val PHARMACY_MEDICINES_GET_BY_ID = "/pharmacies/{pid}/medicines/{mid}"
    const val PHARMACY_RATINGS = "/pharmacies/{pid}/ratings"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"

    fun getMedicines(limit: Long, offset: Long): String {
        return "$MEDICINES?limit=$limit&offset=$offset"
    }

    fun getPharmaciesById(id: Long): String {
        return PHARMACIES_GET_BY_ID.replace("{pid}", id.toString())
    }
}
