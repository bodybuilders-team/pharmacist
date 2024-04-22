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
    const val USER_FAVORITE_PHARMACIES_GET_BY_ID = "/users/{uid}/favorite-pharmacies/{pid}"

    const val PHARMACIES = "/pharmacies"
    const val PHARMACIES_GET_BY_ID = "/pharmacies/{pid}"
    const val PHARMACY_MEDICINES = "/pharmacies/{pid}/medicines"
    const val PHARMACY_MEDICINES_GET_BY_ID = "/pharmacies/{pid}/medicines/{mid}"
    const val PHARMACY_RATE = "/pharmacies/{pid}/rate"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"
    const val MEDICINE_NOTIFICATIONS = "/medicine-notifications"

    const val CREATE_SIGNED_URL = "/create-signed-url"

    fun users(): URI = URI(USERS)
    fun usersLogin(): URI = URI(USERS_LOGIN)
    fun usersLogout(): URI = URI(USERS_LOGOUT)
    fun userById(uid: Long): URI = UriTemplate(USERS_GET_BY_ID).expand(uid)
    fun userFavoritePharmacies(uid: Long): URI = UriTemplate(USER_FAVORITE_PHARMACIES).expand(uid)
    fun userFavoritePharmacyById(uid: Long, pid: Long): URI =
        UriTemplate(USER_FAVORITE_PHARMACIES_GET_BY_ID).expand(uid, pid)

    fun pharmacies(): URI = URI(PHARMACIES)
    fun pharmacyById(pid: Long): URI = UriTemplate(PHARMACIES_GET_BY_ID).expand(pid)
    fun pharmacyMedicines(pid: Long): URI = UriTemplate(PHARMACY_MEDICINES).expand(pid)
    fun pharmacyMedicineById(pid: Long, mid: Long): URI = UriTemplate(PHARMACY_MEDICINES_GET_BY_ID).expand(pid, mid)
    fun pharmacyRate(pid: Long): URI = UriTemplate(PHARMACY_RATE).expand(pid)

    fun medicines(): URI = URI(MEDICINES)
    fun medicineById(mid: Long): URI = UriTemplate(MEDICINES_GET_BY_ID).expand(mid)
    fun medicineNotifications(): URI = URI(MEDICINE_NOTIFICATIONS)
}