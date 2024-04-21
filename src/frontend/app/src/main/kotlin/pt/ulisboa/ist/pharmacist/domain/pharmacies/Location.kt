package pt.ulisboa.ist.pharmacist.domain.pharmacies

import com.google.android.gms.maps.model.LatLng

/**
 * A location in the world.
 *
 * @property lat the latitude
 * @property lon the longitude
 */
data class Location(val lat: Double, val lon: Double) {
    override fun toString(): String {
        return "$lat,$lon"
    }

    /**
     * Converts this location to a [LatLng].
     *
     * @return the [LatLng] representation of this location
     */
    fun toLatLng(): LatLng {
        return LatLng(lat, lon)
    }
}
