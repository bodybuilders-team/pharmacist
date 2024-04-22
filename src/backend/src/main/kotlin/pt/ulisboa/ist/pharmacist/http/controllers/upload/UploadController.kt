package pt.ulisboa.ist.pharmacist.http.controllers.upload

import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.HttpMethod
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.common.net.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.controllers.upload.models.SignedUrlModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.utils.ServerConfiguration
import java.util.concurrent.TimeUnit


@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@Authenticated
class UploadController(val serverConfiguration: ServerConfiguration) {

    /**
     * Generates a signed URL for a PUT request to upload an object to a bucket.
     *
     * @param projectId the project id
     * @param bucketName the bucket name
     * @param objectName the object name
     *
     * @return the signed URL
     */
    private fun generateV4GetObjectSignedUrl(
        projectId: String,
        bucketName: String,
        objectName: String
    ): SignedUrlModel {

        val credentials: Credentials = GoogleCredentials.fromStream(
            UploadController::class.java.getResourceAsStream(serverConfiguration.googleCredentials)
        )

        val storage: Storage = StorageOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(projectId).build().service

        // Define resource
        val blobInfo: BlobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build()

        // Generate Signed URL
        val extensionHeaders: MutableMap<String, String> = HashMap()
        extensionHeaders[CONTENT_TYPE] = MediaType.APPLICATION_OCTET_STREAM_VALUE

        val url = storage.signUrl(
            blobInfo,
            15,
            TimeUnit.MINUTES,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withExtHeaders(extensionHeaders),
            Storage.SignUrlOption.withV4Signature()
        )

        return SignedUrlModel(
            signedUrl = url.toString(),
            objectName = objectName
        )
    }

    /**
     * Handles the request to create a signed URL to upload an object to a bucket.
     *
     * @return the response to the request with the signed URL
     */
    @PostMapping(Uris.CREATE_SIGNED_URL)
    fun createSignedUrl(): SignedUrlModel {
        val uuid = java.util.UUID.randomUUID().toString()

        return generateV4GetObjectSignedUrl(
            serverConfiguration.googleProjectId,
            serverConfiguration.googleBucketName,
            uuid
        )
    }
}