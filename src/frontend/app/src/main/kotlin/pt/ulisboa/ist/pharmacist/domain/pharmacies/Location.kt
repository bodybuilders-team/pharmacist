package pt.ulisboa.ist.pharmacist.domain.pharmacies

import com.google.android.gms.maps.model.LatLng

data class Location(val lat: Double, val lon: Double) {
    override fun toString(): String {
        return "$lat,$lon"
    }
}

fun Location.toLatLng(): LatLng {
    return LatLng(lat, lon)
}