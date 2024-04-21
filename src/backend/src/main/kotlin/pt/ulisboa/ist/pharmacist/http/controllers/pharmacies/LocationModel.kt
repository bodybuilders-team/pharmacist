package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.LocationDto

data class LocationModel(
    val lat: Double,
    val lon: Double
) {
    constructor(location: LocationDto) : this(
        lat = location.lat,
        lon = location.lon
    )
}
