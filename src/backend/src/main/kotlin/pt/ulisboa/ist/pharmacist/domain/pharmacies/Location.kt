package pt.ulisboa.ist.pharmacist.domain.pharmacies

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A Location.
 *
 * @property lat the latitude
 * @property lon the longitude
 */
data class Location(
    val lat: Double,
    val lon: Double
) {

    /**
     * Calculates the distance to another location.
     *
     * @param location the other location
     * @return the distance to the other location in kilometers
     */
    fun distanceTo(location: Location): Double {
        val latDistance = Math.toRadians(lat - location.lat)
        val lonDistance = Math.toRadians(lon - location.lon)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                (cos(Math.toRadians(lat)) * cos(Math.toRadians(location.lat)) *
                        sin(lonDistance / 2) * sin(lonDistance / 2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    companion object {

        /**
         * Parses a location from a string.
         *
         * @param location the location string
         * @return the location or null if the string is not a valid location
         */
        fun parse(location: String): Location? {
            val locationParts = location.split(",")
            if (locationParts.size != 2) return null
            val lat = locationParts[0].toDoubleOrNull() ?: return null
            val lon = locationParts[1].toDoubleOrNull() ?: return null

            return Location(lat, lon)
        }

        const val EARTH_RADIUS = 6371.0
    }
}
