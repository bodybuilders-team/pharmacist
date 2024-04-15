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
    const val USERS_REFRESH_TOKEN = "/users/refresh-token"
    const val USERS_GET_BY_ID = "/users/{uid}"
    const val USERS_FAVORITE_PHARMACIES = "/users/{uid}/favorite-pharmacies"

    const val PHARMACIES = "/pharmacies"
    const val PHARMACIES_GET_BY_ID = "/pharmacies/{pid}"
    const val PHARMACIES_MEDICINES = "/pharmacies/{pid}/medicines"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"

    fun users(): URI = URI(USERS)
    fun usersLogin(): URI = URI(USERS_LOGIN)
    fun usersLogout(): URI = URI(USERS_LOGOUT)
    fun usersRefreshToken(): URI = URI(USERS_REFRESH_TOKEN)
    fun userById(uid: Int): URI = UriTemplate(USERS_GET_BY_ID).expand(uid)
    fun userFavoritePharmacies(uid: Int): URI = UriTemplate(USERS_FAVORITE_PHARMACIES).expand(uid)

    fun pharmacies(): URI = URI(PHARMACIES)
    fun pharmacyById(pid: Int): URI = UriTemplate(PHARMACIES_GET_BY_ID).expand(pid)
    fun pharmacyMedicines(pid: Int): URI = UriTemplate(PHARMACIES_MEDICINES).expand(pid)

    fun medicines(): URI = URI(MEDICINES)
    fun medicineById(mid: Int): URI = UriTemplate(MEDICINES_GET_BY_ID).expand(mid)
}