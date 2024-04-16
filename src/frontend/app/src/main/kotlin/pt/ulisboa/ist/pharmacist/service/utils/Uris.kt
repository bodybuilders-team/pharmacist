package pt.ulisboa.ist.pharmacist.service.utils

import java.net.URI

/**
 * The URIs of the API.
 */
object Uris {
    const val USERS = "/users"
    const val USERS_LOGIN = "/users/login"
    const val USERS_LOGOUT = "/users/logout"
    const val USERS_REFRESH_TOKEN = "/users/refresh-token"
    const val USERS_GET_BY_ID = "/users/{uid}"
    const val USERS_FAVORITE_PHARMACIES = "/users/{uid}/favorite-pharmacies"

    const val PHARMACIES = "/pharmacies"
    const val PHARMACIES_GET_BY_ID = "/pharmacies/{pid}"
    const val PHARMACIES_MEDICINES = "/pharmacies/{pid}/medicines"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"
}
