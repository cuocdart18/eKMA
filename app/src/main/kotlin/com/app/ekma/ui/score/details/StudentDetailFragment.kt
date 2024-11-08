package com.app.ekma.ui.score.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_IS_MY_MINISTUDENT_ID
import com.app.ekma.common.KEY_PASS_MINISTUDENT_ID
import com.app.ekma.data.models.Student
import com.app.ekma.databinding.FragmentScoreStudentDetailBinding
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDetailFragment : BaseFragment() {
    override val TAG = StudentDetailFragment::class.java.simpleName
    private lateinit var binding: FragmentScoreStudentDetailBinding
    private val viewModel by viewModels<StudentDetailViewModel>()

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreStudentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewFakeStatus.updateLayoutParams<ConstraintLayout.LayoutParams> {
            height = getStatusBarHeight
        }
        showLoadingLayout()
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
        binding.layoutGpaInfoStudent.student = student
        binding.layoutTheNumberOfPassedFailedSubjects.student = student
        // set adapter data for rcv
        val studentDetailAdapter = StudentDetailAdapter() { score -> }
        studentDetailAdapter.setScores(student.scores)

        // update list to UI
        binding.layoutListOfScoreStudent.rvScores.layoutManager =
            LinearLayoutManager(context?.applicationContext)
        binding.layoutListOfScoreStudent.rvScores.isFocusable = false
        binding.layoutListOfScoreStudent.rvScores.isNestedScrollingEnabled = false
        binding.layoutListOfScoreStudent.rvScores.adapter = studentDetailAdapter
        binding.layoutGpaInfoStudent.progressCircularGpa
            .setProgress(100 - ((student.avgScore.toFloat() / 4.0f) * 100).toInt())
    }

    private fun showLoadingLayout() {
        binding.layoutDogLoading.layoutDogLoadingContainer.visibility = View.VISIBLE
        binding.layoutDetailContainer.visibility = View.GONE
    }

    private fun hideLoadingLayout() {
        binding.layoutDogLoading.layoutDogLoadingContainer.visibility = View.GONE
        binding.layoutDetailContainer.visibility = View.VISIBLE
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