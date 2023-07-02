package com.example.kmatool.ui.score.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.KEY_PASS_MINISTUDENT_ID
import com.example.kmatool.data.models.Student
import com.example.kmatool.databinding.FragmentScoreStudentDetailBinding
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

        binding.layoutGpaInfoStudent.student = student
        binding.layoutTheNumberOfPassedFailedSubjects.student = student
        // set adapter data for rcv
        val studentDetailAdapter = StudentDetailAdapter() { score ->
//            onClickScoreItemInList(score)
        }
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
}