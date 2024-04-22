package pt.ulisboa.ist.pharmacist.http.controllers.upload

import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.HttpMethod
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.util.concurrent.TimeUnit
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.utils.ServerConfiguration


@RestController
@RequestMapping(produces = ["application/json"])
@Authenticated
class UploadController(val serverConfiguration: ServerConfiguration) {


    fun generateV4GetObjectSignedUrl(
        projectId: String, bucketName: String, objectName: String
    ): SignedUrlModel {

        val credentials: Credentials = GoogleCredentials.fromStream(
            UploadController::class.java.getResourceAsStream("/sa-private-key.json")
        )

        val storage: Storage = StorageOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(projectId).build().getService()

        // Define resource
        val blobInfo: BlobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build()


        // Generate Signed URL
        val extensionHeaders: MutableMap<String, String> = HashMap()
        extensionHeaders["Content-Type"] = "application/octet-stream"

        val url =
            storage.signUrl(
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

    @PostMapping("/create-signed-url")
    fun createSignedUrl(): SignedUrlModel {
        val uuid = java.util.UUID.randomUUID().toString()
        return generateV4GetObjectSignedUrl("pharmacist-420716", "pharmacist-g03", uuid)
    }

}

data class SignedUrlModel(
    val signedUrl: String,
    val objectName: String
)