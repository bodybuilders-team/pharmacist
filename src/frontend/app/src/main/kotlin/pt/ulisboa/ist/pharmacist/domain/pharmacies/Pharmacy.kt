package pt.ulisboa.ist.pharmacist.domain.pharmacies

import com.google.android.gms.maps.model.LatLng

data class Pharmacy(
    var id: Long,
    val name: String,
    val location: LatLng,
    val picture: String
)