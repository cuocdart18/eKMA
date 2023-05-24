package com.example.kmatool.ui.score.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentScoreStudentDetailBinding
import com.example.kmatool.data.models.Student
import com.example.kmatool.common.KEY_PASS_MINISTUDENT_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDetailFragment : BaseFragment() {
    override val TAG = StudentDetailFragment::class.java.simpleName
    private lateinit var binding: FragmentScoreStudentDetailBinding
    private val viewModel by viewModels<StudentDetailViewModel>()
    private var studentId: String = ""

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
        binding.studentDetailVM = viewModel
        receiveData()
        viewModel.getDetailStudent(studentId) { student ->
            if (student != null) {
                showDetailStudent(student)
            } else {
                showToast("Something went wrong")
            }
        }
    }

    private fun receiveData() {
        val bundle = arguments
        bundle?.let {
            studentId = it.getString(KEY_PASS_MINISTUDENT_ID).toString()
        }
        logInfo("receiveDataFromScoreMainFragment student id = $studentId")
    }

    private fun showDetailStudent(student: Student) {
        logDebug("showDetailStudent id = ${student.id}")
        binding.student = student
        // set adapter data for rcv
        val studentDetailAdapter = StudentDetailAdapter() { score ->
//            onClickScoreItemInList(score)
        }
        studentDetailAdapter.setScores(student.scores)

        // update list to UI
        binding.rvScores.layoutManager = LinearLayoutManager(context?.applicationContext)
        binding.rvScores.isFocusable = false
        binding.rvScores.isNestedScrollingEnabled = false
        binding.rvScores.adapter = studentDetailAdapter
    }

    /*private fun onClickScoreItemInList(score: Score) {
        logInfo("onClickScoreItemInList score = $score")
        viewModel.getStatisticSubject(score) { statisticSubject ->
            showStatisticSubject(statisticSubject)
        }
    }*/

    /*private fun showStatisticSubject(statisticSubject: StatisticSubject) {
        logDebug("showStatisticSubject")
        // action (show data to dialog)
        val bundle = bundleOf(
            KEY_PASS_STATISTIC_SUBJECT to statisticSubject
        )
        navigateToFragment(R.id.statisticSubjectDialogFragment, bundle)
    }*/

    /*private fun onClickTagFooter() {
        logDebug("onClickTagFooter")
        // action
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(KIT_URL)
        startActivity(intent)
    }*/
}