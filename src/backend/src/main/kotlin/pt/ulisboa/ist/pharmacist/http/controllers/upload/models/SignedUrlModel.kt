package pt.ulisboa.ist.pharmacist.http.controllers.upload.models

/**
 * A Signed URL Model.
 *
 * @property signedUrl the signed URL
 * @property objectName the object name
 */
data class SignedUrlModel(
    val signedUrl: String,
    val objectName: String
)