package com.app.ekma.ui.chat.image_viewer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_IMAGE_URL
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.animation.visible
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.FragmentImageViewerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cuocdat.activityutils.getStatusBarHeight
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
                    showToast("Ảnh đã được lưu")
                }
            } else {
                showToast("Không thể tải xuống hình ảnh vì chưa được cấp quyền")
            }
        }

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun getDataBinding() = FragmentImageViewerBinding.inflate(layoutInflater)

    override fun initDataByArgs() {
        val bundle = arguments
        bundle?.let {
            viewModel.imageUrl = it.getString(KEY_PASS_IMAGE_URL).toString()
        }
    }

    override fun initView() {
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }

        Glide.with(requireContext())
            .load(Uri.parse(viewModel.imageUrl))
            .placeholder(R.drawable.default_image_message)
            .error(R.drawable.default_image_message)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pgrLoading.visible(true) {}
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pgrLoading.gone(true)
                    return false
                }
            })
            .into(binding.imvImage)
    }

    override fun addEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btnDownload.setOnSingleClickListener {
            checkWritePermission()
        }

        binding.btnClose.performClick {
            parentFragmentManager.popBackStack()
        }
    }

    override fun addObservers() {
        collectLatestFlow(viewModel.showDownloadLoading) {
            if (it) {
                binding.pgrLoading.visible(true) {}
            } else {
                binding.pgrLoading.gone(true)
            }
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
                        showToast("Ảnh đã được lưu")
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
                showToast("Ảnh đã được lưu")
            }
        }
    }
}