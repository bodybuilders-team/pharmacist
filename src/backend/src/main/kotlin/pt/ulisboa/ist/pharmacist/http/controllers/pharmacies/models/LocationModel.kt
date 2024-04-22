package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.LocationDto

/**
 * The model of a location.
 *
 * @property lat the latitude
 * @property lon the longitude
 */
data class LocationModel(
    val lat: Double,
    val lon: Double
) {
    constructor(location: LocationDto) : this(
        lat = location.lat,
        lon = location.lon
    )
}
