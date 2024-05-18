package pt.ulisboa.ist.pharmacist.repository.network.services.upload.models.createSignedUrl

data class SignedUrlOutputModel(
    val signedUrl: String,
    val url: String
)