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
    const val USERS_GET_BY_ID = "/users/{uid}"
    const val USER_FAVORITE_PHARMACIES = "/users/{uid}/favorite-pharmacies"
    // const val USERS_REFRESH_TOKEN = "/users/refresh-token"

    const val PHARMACIES = "/pharmacies"
    const val PHARMACIES_GET_BY_ID = "/pharmacies/{pid}"
    const val PHARMACY_MEDICINES = "/pharmacies/{pid}/medicines"
    const val PHARMACY_MEDICINES_GET_BY_ID = "/pharmacies/{pid}/medicines/{mid}"
    const val PHARMACY_RATINGS = "/pharmacies/{pid}/ratings"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"

    fun users(): URI = URI(USERS)
    fun usersLogin(): URI = URI(USERS_LOGIN)
    fun usersLogout(): URI = URI(USERS_LOGOUT)
    fun userById(uid: Long): URI = UriTemplate(USERS_GET_BY_ID).expand(uid)
    fun userFavoritePharmacies(uid: Long): URI = UriTemplate(USER_FAVORITE_PHARMACIES).expand(uid)
    // fun usersRefreshToken(): URI = URI(USERS_REFRESH_TOKEN)

    fun pharmacies(): URI = URI(PHARMACIES)
    fun pharmacyById(pid: Long): URI = UriTemplate(PHARMACIES_GET_BY_ID).expand(pid)
    fun pharmacyMedicines(pid: Long): URI = UriTemplate(PHARMACY_MEDICINES).expand(pid)

    fun medicines(): URI = URI(MEDICINES)
    fun medicineById(mid: Long): URI = UriTemplate(MEDICINES_GET_BY_ID).expand(mid)
}