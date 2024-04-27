package pt.ulisboa.ist.pharmacist.service.http.services.upload.models.createSignedUrl

data class SignedUrlOutputModel(
    val signedUrl: String,
    val url: String
)