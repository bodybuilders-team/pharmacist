package pt.ulisboa.ist.pharmacist.domain.pharmacies

import com.google.android.gms.maps.model.LatLng

data class Pharmacy(
    var pharmacyId: Long,
    val name: String,
    val location: LatLng,
    val pictureUrl: String
)