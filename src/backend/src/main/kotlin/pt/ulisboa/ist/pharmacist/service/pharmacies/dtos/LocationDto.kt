package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

data class LocationDto(
    val lat: Double,
    val lon: Double
) {
    constructor(location: Location) : this(
        lat = location.lat,
        lon = location.lon
    )
}