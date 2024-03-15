import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.tubespbd.R
import com.example.tubespbd.databinding.FragmentScanBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraHandler(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val binding: FragmentScanBinding) {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture

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

    fun takePicture() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(getOutputFile()).build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(getOutputFile())
                    Log.d("CameraHandler", "Photo capture succeeded: $savedUri")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraHandler", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun getOutputFile(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        return File(outputDirectory, fileName)
    }

//    private fun showPicture(): File {
//
//    }

}
