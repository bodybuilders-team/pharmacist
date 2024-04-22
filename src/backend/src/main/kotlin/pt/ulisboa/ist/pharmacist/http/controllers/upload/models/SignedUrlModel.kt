package pt.ulisboa.ist.pharmacist.http.controllers.upload.models

/**
 * A Signed URL Model.
 *
 * @property signedUrl the signed URL
 * @property url the object url
 */
data class SignedUrlModel(
    val signedUrl: String,
    val url: String
)