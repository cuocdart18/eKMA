package com.app.ekma.ui.infor

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_IS_MY_MINISTUDENT_ID
import com.app.ekma.common.KEY_PASS_MINISTUDENT_ID
import com.app.ekma.common.pattern.singleton.DownloadAvatarSuccess
import com.app.ekma.common.pattern.singleton.MainBottomNavigation
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.app.openWeb
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.FragmentInformationBinding
import com.app.ekma.ui.chat.activity.ChatActivity
import com.app.ekma.ui.login.LoginActivity
import com.app.ekma.ui.score.details.StudentDetailFragment
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment<FragmentInformationBinding>() {
    override val TAG = InformationFragment::class.java.simpleName
    private val viewModel by viewModels<InformationViewModel>()

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) {}

    override fun getDataBinding() = FragmentInformationBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }
        MainBottomNavigation.setData(false)

        setUpProfile()
        setUpSetting()

        // ask permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission()
        }
    }

    private fun setUpProfile() {
        binding.tvProfileName.text = ProfileSingleton().displayName
        binding.tvProfileId.text = ProfileSingleton().studentCode
        viewModel.getImageProfile { setImageUri(it) }
        DownloadAvatarSuccess().observe(viewLifecycleOwner) { path ->
            if (path.isEmpty()) return@observe
            setImageUri(Uri.parse(path))
        }
        binding.civProfileImage.setOnSingleClickListener { onClickChangeProfile() }

        viewModel.getProfileDetail()
        collectLatestFlow(viewModel.profileDetail) {
            it?.let {
                binding.layoutInfor.tvDoBContent.text = it.birthday
                binding.layoutInfor.tvGenderContent.text = it.gender
                binding.layoutInfor.tvHometownContent.text = it.hometown
                binding.layoutInfor.tvNumberContent.text = it.phoneNumber
                binding.layoutInfor.tvEmailContent.text = it.email
            }
        }
    }

    private fun setUpSetting() {
        viewModel.getIsNotifyEvent {
            binding.layoutSetting.swNoti.isChecked = it
        }

        binding.layoutSetting.layoutMyScore.performClick {
            onClickMyScore()
        }

        binding.layoutSetting.layoutUpdate.performClick {
            onClickUpdateSchedule()
        }

        binding.layoutSetting.layoutNoti.performClick {
            binding.layoutSetting.swNoti.apply {
                isChecked = !isChecked
                onChangedNotifyEvent(isChecked)
            }
        }

        binding.layoutSetting.layoutAboutMe.performClick {
            requireContext().openWeb("https://github.com/cuocdart18")
        }

        binding.layoutSetting.layoutRate.performClick {
            requireContext().openWeb("https://github.com/cuocdart18")
        }

        binding.layoutSetting.layoutLogOut.performClick {
            onClickLogOut()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        when {
            checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                showDialogNotifyPermissionRationale()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun showDialogNotifyPermissionRationale() {
        var dialog: Dialog? = null

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun onClickYes() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
            dialog?.dismiss()
        }

        fun onClickNo() {
            dialog?.dismiss()
        }

        dialog = showAlertDialog(
            R.drawable.notify_amico,
            "Nhận thông báo sự kiện ?",
            "Với những thiết bị chạy Android 13 trở lên, ứng dụng cần được cấp quyền hiện thông báo để sử dụng tính năng nhắc nhở",
            "Đóng",
            "Huỷ bỏ",
            { onClickYes() },
            { onClickNo() },
            true
        )
        dialog.show()
    }

    private fun onClickChangeProfile() {
        viewModel.onChangeProfileImage(requireContext()) { uri ->
            setImageUri(uri)
        }
    }

    private fun onClickMyScore() {
        val bundle = bundleOf(
            KEY_PASS_MINISTUDENT_ID to ProfileSingleton().studentCode,
            KEY_PASS_IS_MY_MINISTUDENT_ID to true
        )
        parentFragmentManager.commit {
            replace<StudentDetailFragment>(R.id.fragment_container_view, args = bundle)
            setReorderingAllowed(true)
            addToBackStack(StudentDetailFragment::class.java.simpleName)
        }
    }

    private fun onClickUpdateSchedule() {
        var dialog: Dialog? = null
        fun onClickYes() {
            viewModel.updateSchedule(requireContext()) {
                dialog?.dismiss()
            }
        }

        fun onClickNo() {
            dialog?.dismiss()
        }

        dialog = showAlertDialog(
            R.drawable.in_sync_red_500dp,
            "Cập nhật thời khoá biểu ?",
            "Nếu bạn đã thay đổi lịch trên hệ thống, hãy cập nhật lịch mới nhất",
            "Đồng ý",
            "Huỷ bỏ",
            { onClickYes() },
            { onClickNo() },
            false
        )
        dialog.show()
    }

    private fun onChangedNotifyEvent(data: Boolean) {
        viewModel.changedIsNotifyEvents(requireContext(), data)
    }

    private fun onClickChat() {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        startActivity(intent)
    }

    private fun onChangedLanguage() {
    }

    private fun onClickLogOut() {
        var dialog: Dialog? = null
        fun onClickYes() {
            viewModel.signOut(requireContext()) {
                dialog?.dismiss()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        fun onClickNo() {
            dialog?.dismiss()
        }

        dialog = showAlertDialog(
            R.drawable.secure_data_red_500dp,
            "Đăng xuất ?",
            "",
            "Đồng ý",
            "Huỷ bỏ",
            { onClickYes() },
            { onClickNo() },
            false
        )
        dialog.show()
    }

    private fun setImageUri(uri: Uri) {
        if (uri.toString().isBlank()) {
            binding.civProfileImage.setBackgroundResource(R.drawable.user)
        } else {
            binding.civProfileImage.setImageURI(uri)
        }
    }
}