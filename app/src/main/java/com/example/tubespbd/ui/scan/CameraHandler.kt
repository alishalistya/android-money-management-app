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
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.example.tubespbd.databinding.FragmentScanBinding
import com.example.tubespbd.databinding.FragmentTwibbonBinding
import com.google.common.util.concurrent.ListenableFuture
import okhttp3.internal.notify
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class CameraHandler<T>(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val binding: T
) where T : ViewBinding {
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private var capturedImageFile: File? = null
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider, cameraSelector: CameraSelector = lensFacing) {
        val previewView = when (binding) {
            is FragmentTwibbonBinding -> binding.previewView
            is FragmentScanBinding -> binding.previewView
            else -> throw IllegalArgumentException("Unsupported binding type")
        }
        val preview : Preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

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

    fun takePicture(callback: (Uri) -> Unit){
        capturedImageFile = getOutputFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(capturedImageFile!!).build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(capturedImageFile)
                    Log.d("CameraHandler", "Photo capture succeeded: $savedUri")
                    callback(savedUri)
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
            mediaDir = mediaDirs[0]
            if (!mediaDir.exists() && !mediaDir.mkdirs()) {
                Log.e("Media dir", "Failed to create media directory")
                mediaDir = null
            }
        }
        val outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        return File(outputDirectory, fileName)
    }

    fun flipCamera(){
        lensFacing = if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
       startCamera()
    }
}
