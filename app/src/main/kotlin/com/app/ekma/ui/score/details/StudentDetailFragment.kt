package com.app.ekma.ui.score.details

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_IS_MY_MINISTUDENT_ID
import com.app.ekma.common.KEY_PASS_MINISTUDENT_ID
import com.app.ekma.common.jsonObjectToString
import com.app.ekma.common.makeGone
import com.app.ekma.common.makeVisible
import com.app.ekma.common.super_utils.activity.collectLatestFlow
import com.app.ekma.common.super_utils.animation.gone
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.data.models.Student
import com.app.ekma.databinding.FragmentScoreStudentDetailBinding
import com.app.ekma.ui.score.details.adapter.ItemScoreBannerFragment
import com.app.ekma.ui.score.details.adapter.ScoreViewPagerAdapter
import com.app.ekma.ui.score.details.adapter.StudentDetailAdapter
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDetailFragment : BaseFragment<FragmentScoreStudentDetailBinding>() {
    override val TAG = StudentDetailFragment::class.java.simpleName
    private val viewModel by viewModels<StudentDetailViewModel>()

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun getDataBinding() = FragmentScoreStudentDetailBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        receiveData()

        viewModel.getDetailStudent { student ->
            if (student != null) {
                showDetailStudent(student)
                hideLoadingLayout()
            } else {
                showError(getString(R.string.desc_error_loading))
            }
        }
    }

    private fun receiveData() {
        val bundle = arguments
        bundle?.let {
            viewModel.studentId = it.getString(KEY_PASS_MINISTUDENT_ID).toString()
            viewModel.isMyStudentId = it.getBoolean(KEY_PASS_IS_MY_MINISTUDENT_ID)
        }
    }

    private fun showDetailStudent(student: Student) {
        // setup for score banner
        val studentBanner = jsonObjectToString(
            student.copy(scores = listOf()).apply { sizeScores = student.scores.size })
        val fragments = List(viewModel.sizeOfBanner) {
            ItemScoreBannerFragment.newInstance(it, studentBanner)
        }
        val adapter = ScoreViewPagerAdapter(requireActivity(), fragments)
        binding.vpBanner.adapter = adapter
        binding.vpIndicator.attachTo(binding.vpBanner)
        viewModel.startAutoScroll()
        collectLatestFlow(viewModel.autoScrollableBanner) { pos ->
            binding.vpBanner.setCurrentItem(pos, true)
        }

        // setup for detail score
        val studentDetailAdapter = StudentDetailAdapter { _ -> }
        studentDetailAdapter.setScores(student.scores)

        binding.rvScore.layoutManager = LinearLayoutManager(requireContext())
        binding.rvScore.isFocusable = false
        binding.rvScore.itemAnimator = null
        binding.rvScore.adapter = studentDetailAdapter
    }

    private fun setupUI() {
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }

        showLoadingLayout()

        binding.btnBack.performClick {
            binding.btnBack.gone(true) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun showLoadingLayout() {
        binding.layoutDogLoading.layoutDogLoadingContainer.makeVisible()
        binding.layoutScoreRoot.makeGone()
    }

    private fun hideLoadingLayout() {
        binding.layoutDogLoading.layoutDogLoadingContainer.makeGone()
        binding.layoutScoreRoot.makeVisible()
    }

    private fun showError(msg: String) {
        binding.layoutDogLoading.tvMessageHelper.text = msg
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onPause() {
        super.onPause()
        callback.remove()
    }
}