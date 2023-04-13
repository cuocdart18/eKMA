package com.example.kmatool.ui.score.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.databinding.FragmentScoreStudentDetailBinding
import com.example.kmatool.data.models.Score
import com.example.kmatool.data.models.StatisticSubject
import com.example.kmatool.data.models.Student
import com.example.kmatool.utils.KEY_PASS_MINISTUDENT_ID
import com.example.kmatool.utils.KEY_PASS_STATISTIC_SUBJECT
import com.example.kmatool.utils.KIT_URL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDetailFragment : BaseFragment() {
    override val TAG = StudentDetailFragment::class.java.simpleName
    private lateinit var binding: FragmentScoreStudentDetailBinding
    private val studentDetailViewModel by viewModels<StudentDetailViewModel>()
    private var studentId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreStudentDetailBinding.inflate(inflater, container, false)
        binding.studentDetailVM = studentDetailViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // receive data from main fragment
        receiveDataFromScoreMainFragment()
        // show detail student
        studentDetailViewModel.getDetailStudent(studentId) { student ->
            showDetailStudent(student)
        }
    }

    private fun receiveDataFromScoreMainFragment() {
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
            onClickScoreItemInList(score)
        }
        studentDetailAdapter.setScores(student.scores)

        // update list to UI
        binding.rvScores.layoutManager = LinearLayoutManager(context?.applicationContext)
        binding.rvScores.isFocusable = false
        binding.rvScores.isNestedScrollingEnabled = false
        binding.rvScores.adapter = studentDetailAdapter
    }

    private fun onClickScoreItemInList(score: Score) {
        logInfo("onClickScoreItemInList score = $score")
        // action (get statistic subject)
        studentDetailViewModel.getStatisticSubject(score) { statisticSubject ->
            showStatisticSubject(statisticSubject)
        }
    }

    private fun showStatisticSubject(statisticSubject: StatisticSubject) {
        logDebug("showStatisticSubject")
        // action (show data to dialog)
        val bundle = bundleOf(
            KEY_PASS_STATISTIC_SUBJECT to statisticSubject
        )
        navigateToFragment(R.id.statisticSubjectDialogFragment, bundle)
    }

    private fun onClickTagFooter() {
        logDebug("onClickTagFooter")
        // action
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(KIT_URL)
        startActivity(intent)
    }
}