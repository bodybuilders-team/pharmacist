package pt.ulisboa.ist.pharmacist.service.utils

import android.net.Uri
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

/**
 * The URIs of the API.
 */
object Uris {
    const val CREATE_SIGNED_URL = "/create-signed-url"
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
    const val PHARMACY_RATINGS = "/pharmacies/{pid}/rate"

    const val MEDICINES = "/medicines"
    const val MEDICINES_GET_BY_ID = "/medicines/{mid}"

    const val MEDICINE_NOTIFICATIONS = "/medicine-notifications"

    fun getMedicines(substring: String, location: Location?, limit: Long, offset: Long) =
        "$MEDICINES?substring=$substring${if (location == null) "" else "&location=$location"}&limit=$limit&offset=$offset"

    fun getPharmacies(medicineId: Long?, limit: Long?, offset: Long?): String {
        return Uri.Builder().apply {
            appendPath(PHARMACIES.drop(1))
            if (medicineId != null)
                appendQueryParameter("medicine", medicineId.toString())
            if (limit != null)
                appendQueryParameter("limit", limit.toString())
            if (offset != null)
                appendQueryParameter("offset", offset.toString())
        }.build().toString()
    }

    fun getPharmacyById(id: Long) = PHARMACIES_GET_BY_ID.replace("{pid}", id.toString())

    fun getMedicineById(pid: Long) = MEDICINES_GET_BY_ID.replace("{mid}", pid.toString())

    fun listAvailableMedicines(pharmacyId: Long, limit: Long?, offset: Long?): String {
        return PHARMACY_MEDICINES
            .replace("{pid}", pharmacyId.toString()) +
                "?${if (limit != null) "limit=$limit" else ""}${if (offset != null) "&offset=$offset" else ""}"
    }

    fun favoritePharmaciesGetById(userId: String, pharmacyId: Long) =
        USER_FAVORITE_PHARMACIES_GET_BY_ID
            .replace("{uid}", userId)
            .replace("{pid}", pharmacyId.toString())

    fun ratePharmacy(pharmacyId: Long): String {
        return PHARMACY_RATINGS.replace("{pid}", pharmacyId.toString())
    }

    fun changeMedicineStock(pharmacyId: Long, medicineId: Long): String =
        PHARMACY_MEDICINES_GET_BY_ID
            .replace("{pid}", pharmacyId.toString())
            .replace("{mid}", medicineId.toString())

    fun addNewMedicineToPharmacy(pharmacyId: Long, medicineId: Long): String {
        return PHARMACY_MEDICINES_GET_BY_ID
            .replace("{pid}", pharmacyId.toString())
            .replace("{mid}", medicineId.toString())
    }
}
