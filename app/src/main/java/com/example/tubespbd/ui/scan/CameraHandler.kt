import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.tubespbd.databinding.FragmentScanBinding
import com.google.common.util.concurrent.ListenableFuture
import okhttp3.internal.notify
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class CameraHandler(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val binding: FragmentScanBinding) {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private var capturedImageFile: File? = null
    val lock = Object()

    fun startCamera() {
        // Initialize Preview
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // Add more configuration options here if needed
            .build()

    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val previewView = binding.previewView
        val preview : Preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val cameraSelector =  CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture)
        } catch (exc: Exception) {
            Log.e("CameraHandler", "Use case binding failed", exc)
        }
    }

    fun takePicture(callback: ImageSavedCallback){
        capturedImageFile = getOutputFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(capturedImageFile!!).build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(capturedImageFile)
                    Log.d("CameraHandler", "Photo capture succeeded: $savedUri")
                    callback.onImageSaved(capturedImageFile!!)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraHandler", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun getOutputFile(): File {
        val mediaDirs = context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)
        var mediaDir: File? = null
        if (mediaDirs.isNotEmpty()) {
            // The first element in the array should be the primary external storage
            mediaDir = mediaDirs[0]
            if (!mediaDir.exists() && !mediaDir.mkdirs()) {
                // Handle the case where the directory cannot be created
                Log.e("Media dir", "Failed to create media directory")
                mediaDir = null
            }
        }
        val outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        return File(outputDirectory, fileName)
    }

    fun getCapturedImageFile(): File? {
        return synchronized(this) {
            capturedImageFile
        }
    }

//    private fun showPicture(): File {
//
//    }

}

interface ImageSavedCallback {
    fun onImageSaved(imageFile: File)
}
