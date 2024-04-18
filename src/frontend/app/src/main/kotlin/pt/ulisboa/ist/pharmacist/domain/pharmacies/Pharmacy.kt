package pt.ulisboa.ist.pharmacist.domain.pharmacies

import com.google.android.gms.maps.model.LatLng

data class Pharmacy(
    var id: String,
    val name: String,
    val location: LatLng,
    val picture: String
)