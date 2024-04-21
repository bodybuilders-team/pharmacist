package pt.ulisboa.ist.pharmacist.service.utils

import android.net.Uri
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

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

    fun getMedicines(substring: String, location: Location?, limit: Long, offset: Long): String {
        return "$MEDICINES?substring=$substring${if (location == null) "" else "&location=$location"}&limit=$limit&offset=$offset"
    }

    fun getPharmacies(mid: Long?, limit: Long?, offset: Long?): String {
        return Uri.Builder().apply {
            appendPath(PHARMACIES.slice(1 until PHARMACIES.length))
            if (mid != null)
                appendQueryParameter("medicine", mid.toString())
            if (limit != null)
                appendQueryParameter("limit", limit.toString())
            if (offset != null)
                appendQueryParameter("offset", offset.toString())
        }.build().toString()
    }

    fun getPharmacyById(id: Long): String {
        return PHARMACIES_GET_BY_ID.replace("{pid}", id.toString())
    }

    fun getMedicineById(pid: Long): String {
        return MEDICINES_GET_BY_ID.replace("{mid}", pid.toString())
    }
}
