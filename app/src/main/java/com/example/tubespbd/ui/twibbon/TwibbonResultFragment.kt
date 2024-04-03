package com.example.tubespbd.ui.twibbon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tubespbd.R
import com.example.tubespbd.databinding.FragmentShowTwibbonBinding
import java.io.InputStream

class TwibbonResultFragment : Fragment() {
    private var _binding: FragmentShowTwibbonBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUriString: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowTwibbonBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUriString = arguments?.getString("savedURIPhoto").toString()
        Log.e("TwibbonResultFragment", "Image URI String: $imageUriString")
        val imageUri = Uri.parse(imageUriString)
        displayImage(imageUri)
        binding.recaptureButton.setOnClickListener {
            navigateToTwibbonFragment()
        }
    }

    private fun displayImage(imageUri: Uri) {
        val context = context ?: return
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        var userImageBitmap = BitmapFactory.decodeStream(inputStream)
        val orientation = getExifOrientation(imageUri, requireContext())
        val rotationDegrees = exifToDegrees(orientation)
        if (rotationDegrees != 0f) {
            userImageBitmap = rotateBitmap(userImageBitmap, rotationDegrees)
        }

        val twibbonBitmap = BitmapFactory.decodeResource(resources, R.drawable.twibbon_contrast)
        val combinedBitmap = mergeImages(twibbonBitmap, userImageBitmap)
        binding.imageViewPicture.setImageBitmap(combinedBitmap)
    }

    private fun mergeImages(foreground: Bitmap, background: Bitmap): Bitmap {
        val scaledBackground = Bitmap.createScaledBitmap(
            background,
            foreground.width,
            foreground.height,
            true
        )

        val result = Bitmap.createBitmap(foreground.width, foreground.height, foreground.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(scaledBackground, 0f, 0f, null)
        canvas.drawBitmap(foreground, 0f, 0f, null)
        return result
    }

    private fun getExifOrientation(imageUri: Uri, context: Context): Int {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val exifInterface = inputStream?.let { ExifInterface(it) }
        return exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) ?: ExifInterface.ORIENTATION_NORMAL
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(angle) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun exifToDegrees(exifOrientation: Int): Float {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }

    private fun navigateToTwibbonFragment() {
        findNavController().navigateUp()
    }
}
