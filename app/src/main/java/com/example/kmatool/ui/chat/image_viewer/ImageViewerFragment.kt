package com.example.kmatool.ui.chat.image_viewer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.KEY_PASS_IMAGE_URL
import com.example.kmatool.databinding.FragmentImageViewerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageViewerFragment : BaseFragment() {
    override val TAG = ImageViewerFragment::class.java.simpleName
    private lateinit var binding: FragmentImageViewerBinding
    private val viewModel by viewModels<ImageViewerViewModel>()
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.downloadImage(requireContext()) {
                    showToast("Da download xong")
                }
            } else {
                showToast("Khong the tai xuong hinh anh vi chua duoc cap quyen")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        bundle?.let {
            viewModel.imageUrl = it.getString(KEY_PASS_IMAGE_URL).toString()
        }

        initViews()
    }

    private fun initViews() {
        Glide.with(requireContext())
            .load(Uri.parse(viewModel.imageUrl))
            .placeholder(R.drawable.default_image_message)
            .into(binding.imvImage)

        binding.btnDownload.setOnClickListener {
            checkWritePermission()
        }
    }

    private fun checkWritePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    viewModel.downloadImage(requireContext()) {
                        showToast("Da download xong")
                    }
                }

                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
        } else {
            viewModel.downloadImage(requireContext()) {
                showToast("Da download xong")
            }
        }
    }
}