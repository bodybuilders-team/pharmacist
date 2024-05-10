package pt.ulisboa.ist.pharmacist.ui.screens.shared

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import pt.ulisboa.ist.pharmacist.service.http.PharmacistService
import pt.ulisboa.ist.pharmacist.service.http.connection.isFailure
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

object ImageHandlingUtils {

    /**
     * Handles the image selection result.
     *
     * @param contentResolver the content resolver
     * @param result the activity result
     *
     * @return the output data of the image selection handling
     */
    fun handleImageSelection(
        contentResolver: ContentResolver,
        result: ActivityResult
    ): HandleImageSelectionOutputData? {
        val uri = result.data?.data

        if (uri == null) {
            Log.e("ImageHandlingUtils", "Failed to get uri")
            return null
        }

        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            Log.e("ImageHandlingUtils", "Failed to open input stream")
            return null
        }

        val mimeTypeStr = contentResolver.getType(uri)
        val mimeType = mimeTypeStr?.toMediaType()

        if (mimeType == null) {
            Log.e("ImageHandlingUtils", "Failed to get mime type")
            return null
        }

        return HandleImageSelectionOutputData(inputStream, mimeType)
    }

    /**
     * Handles the take photo result.
     *
     * @param result the activity result
     *
     * @return the output data of the take photo handling
     */
    fun handleTakePhoto(result: ActivityResult): HandleTakePhotoOutputData? {
        val imageBitmap = result.data?.extras?.get("data")

        if (imageBitmap !is Bitmap) {
            Log.e("ImageHandlingUtils", "Failed to get image bitmap")
            return null
        }

        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageBytes = stream.toByteArray()

        return HandleTakePhotoOutputData(imageBytes, "image/jpeg".toMediaType())
    }

    /**
     * Uploads the box photo.
     *
     * @param boxPhotoData the box photo data
     * @param mediaType the media type
     * @param pharmacistService the pharmacist service
     *
     * @return the output data of the box photo upload
     */
    suspend fun uploadBoxPhoto(
        boxPhotoData: ByteArray,
        mediaType: MediaType,
        pharmacistService: PharmacistService
    ): UploadBoxPhotoOutputData? {
        val createSignedUrlResult =
            pharmacistService.uploaderService.createSignedUrl(mediaType.toString())

        if (createSignedUrlResult.isFailure()) {
            Log.e("ImageHandlingUtils", "Failed to create signed URL")
            return null
        }

        Log.d(
            "ImageHandlingUtils",
            "Signed URL created successfully with URL: ${createSignedUrlResult.data.signedUrl} and object name: ${createSignedUrlResult.data.url}"
        )

        val signedUrl = createSignedUrlResult.data.signedUrl

        val uploadResult =
            pharmacistService.uploaderService.uploadBoxPhoto(signedUrl, boxPhotoData, mediaType)

        if (uploadResult.isFailure()) {
            Log.e("ImageHandlingUtils", "Failed to upload box photo")
            return null
        }

        Log.d("ImageHandlingUtils", "Box photo uploaded successfully")

        val boxPhotoUrl = createSignedUrlResult.data.url
        val boxPhoto =
            BitmapFactory.decodeByteArray(boxPhotoData, 0, boxPhotoData.size).asImageBitmap()

        return UploadBoxPhotoOutputData(boxPhotoUrl, boxPhoto)
    }

    /**
     * Gets the chooser intent.
     *
     * @return the chooser intent
     */
    fun getChooserIntent(): Intent {
        val galIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galIntent.addCategory(Intent.CATEGORY_OPENABLE)
        galIntent.setType("image/jpeg")

        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val chooser = Intent.createChooser(galIntent, "Some text here")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(camIntent))

        return chooser
    }

    fun downloadImage(url: String): ImageBitmap? {
        val inputStream = URL(url).openStream()
        return BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
    }
}

data class HandleImageSelectionOutputData(
    val boxPhotoUrl: InputStream,
    val mediaType: MediaType
)

data class HandleTakePhotoOutputData(
    val boxPhotoData: ByteArray,
    val mediaType: MediaType
)

data class UploadBoxPhotoOutputData(
    val boxPhotoUrl: String,
    val boxPhoto: ImageBitmap
)