package pt.ulisboa.ist.pharmacist.repository.remote.upload

data class SignedUrlOutputDto(
    val signedUrl: String,
    val url: String
)