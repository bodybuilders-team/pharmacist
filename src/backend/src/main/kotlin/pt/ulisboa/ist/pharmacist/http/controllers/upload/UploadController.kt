package pt.ulisboa.ist.pharmacist.http.controllers.upload

import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.HttpMethod
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.common.net.HttpHeaders.CONTENT_TYPE
import jakarta.validation.Valid
import java.util.concurrent.TimeUnit
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.controllers.upload.models.SignedUrlInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.upload.models.SignedUrlModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.utils.ServerConfiguration


@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@Authenticated
class UploadController(val serverConfiguration: ServerConfiguration) {

    /**
     * Generates a signed URL for a PUT request to upload an object to a bucket.
     *
     * @return the signed URL
     */
    private fun generateV4GetObjectSignedUrl(
        mimeTypeStr: String,
    ): SignedUrlModel {
        val mimeType = MediaType.parseMediaType(mimeTypeStr)

        if (mimeType.type != "image")
            throw IllegalArgumentException("Invalid MIME type, must be image/*")

        val uuid = java.util.UUID.randomUUID().toString()

        val objectName = "$uuid.${mimeType.subtype}"

        val credentials: Credentials = GoogleCredentials.fromStream(
            UploadController::class.java.getResourceAsStream(serverConfiguration.googleCredentials)
        )

        val storage: Storage = StorageOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(serverConfiguration.googleProjectId).build().service

        // Define resource
        val blobInfo: BlobInfo =
            BlobInfo.newBuilder(BlobId.of(serverConfiguration.googleBucketName, objectName)).build()

        // Generate Signed URL
        val extensionHeaders: MutableMap<String, String> = HashMap()
        extensionHeaders[CONTENT_TYPE] = mimeTypeStr

        val url = storage.signUrl(
            blobInfo,
            SIGNED_URL_DURATION,
            TimeUnit.MINUTES,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withExtHeaders(extensionHeaders),
            Storage.SignUrlOption.withV4Signature()
        )

        return SignedUrlModel(
            signedUrl = url.toString(),
            url = "${serverConfiguration.googleUrl}/${serverConfiguration.googleBucketName}/$objectName"
        )
    }

    /**
     * Handles the request to create a signed URL to upload an object to a bucket.
     *
     * @return the response to the request with the signed URL
     */
    @PostMapping(Uris.CREATE_SIGNED_URL)
    fun createSignedUrl(
        @Valid @RequestBody signedUrlRequest: SignedUrlInputModel
    ): SignedUrlModel = generateV4GetObjectSignedUrl(signedUrlRequest.mimeType)

    companion object {
        private const val SIGNED_URL_DURATION = 15L
    }
}

