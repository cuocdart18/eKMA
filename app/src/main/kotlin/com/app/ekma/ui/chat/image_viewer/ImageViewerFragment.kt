package com.app.ekma.ui.chat.image_viewer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_IMAGE_URL
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.FragmentImageViewerBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageViewerFragment : BaseFragment<FragmentImageViewerBinding>() {
    override val TAG = ImageViewerFragment::class.java.simpleName
    private val viewModel by viewModels<ImageViewerViewModel>()
    private val requestPermissionLauncher = registerForActivityResult(
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

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun getDataBinding() = FragmentImageViewerBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regisOnBackPressed()
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

        binding.btnDownload.setOnSingleClickListener {
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

    private fun regisOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}