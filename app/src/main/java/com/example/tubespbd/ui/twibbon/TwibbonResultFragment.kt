package com.example.tubespbd.ui.twibbon

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tubespbd.databinding.FragmentShowTwibbonBinding

class TwibbonResultFragment : Fragment() {
    private var _binding: FragmentShowTwibbonBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUriString: String

    // On Create Function
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowTwibbonBinding.inflate(inflater, container, false)
        imageUriString = arguments?.getString("savedURIPhoto").toString()
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUriString = arguments?.getString("savedURIPhoto").toString()
        Log.e("error", imageUriString)
        val imageConvertedUri = imageUriString?.let { Uri.parse(it) }
        Log.e("bill", "Image URI: $imageConvertedUri")
        imageConvertedUri?.let {
            displayImage(it)
        }
    }

    private fun displayImage(imageUri: Uri) {
        Log.e("e", "URI: $imageUri")
        binding.imageViewPicture.setImageURI(imageUri)
    }
}