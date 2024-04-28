package pt.ulisboa.ist.pharmacist.http.utils

import org.springframework.web.util.UriTemplate
import java.net.URI

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
    const val MEDICINE_NOTIFICATIONS = "/medicines-notifications"

    const val CREATE_SIGNED_URL = "/create-signed-url"

    fun pharmacyById(pid: Long): URI = UriTemplate(PHARMACIES_GET_BY_ID).expand(pid)

}