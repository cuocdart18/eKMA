package com.app.ekma.ui.infor.main

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.activities.login.LoginActivity
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_IS_MY_MINISTUDENT_ID
import com.app.ekma.common.KEY_PASS_MINISTUDENT_ID
import com.app.ekma.common.pattern.singleton.DownloadAvatarSuccess
import com.app.ekma.common.pattern.singleton.MainBottomNavigation
import com.app.ekma.common.pattern.singleton.ProfileSingleton
import com.app.ekma.databinding.FragmentInformationBinding
import com.app.ekma.ui.chat.list.ListChatFragment
import com.app.ekma.ui.score.details.StudentDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : BaseFragment(),
    SettingFragment.ICallbackOnPreferencesClickListener {
    override val TAG = InformationFragment::class.java.simpleName
    private lateinit var binding: FragmentInformationBinding
    private val viewModel by viewModels<InformationViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
            } else {
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.msgToast.observe(viewLifecycleOwner) {
            showToast(it)
        }
        MainBottomNavigation.setData(false)
        setUpPreferencesSetting()
        setUpProfile()
        // ask permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission()
        }
    }

    private fun setUpPreferencesSetting() {
        childFragmentManager.commit {
            replace(R.id.frm_container, SettingFragment(this@InformationFragment))
            setReorderingAllowed(true)
        }
    }

    private fun setUpProfile() {
        binding.profile = ProfileSingleton()
        viewModel.getImageProfile { setImageUri(it) }
        DownloadAvatarSuccess().observe(viewLifecycleOwner) { path ->
            if (path.isEmpty()) return@observe
            setImageUri(Uri.parse(path))
        }
        binding.civProfileImage.setOnClickListener { onClickChangeProfile() }
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

    override fun onClickMyScore() {
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

    override fun onClickUpdateSchedule() {
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

    override fun onChangedNotifyEvent(data: Boolean) {
        viewModel.changedIsNotifyEvents(requireContext(), data) { }
    }

    override fun onClickChat() {
        MainBottomNavigation.setData(true)
        parentFragmentManager.commit {
            replace<ListChatFragment>(R.id.fragment_container_view)
            setReorderingAllowed(true)
            addToBackStack(ListChatFragment::class.java.simpleName)
        }
    }

    override fun onChangedLanguage() {
    }

    override fun onClickLogOut() {
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