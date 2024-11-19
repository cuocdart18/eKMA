package com.app.ekma.ui.score.details.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.jsonStringToObject
import com.app.ekma.common.makeInVisible
import com.app.ekma.common.makeVisible
import com.app.ekma.data.models.Student
import com.app.ekma.databinding.FragmentScoreBannerBinding
import dagger.hilt.android.AndroidEntryPoint

const val KEY_POSITION = "position"
const val KEY_STUDENT = "student"
const val GPA_POS = 0
const val PASSED_POS = 1
const val FAILED_POS = 2

@AndroidEntryPoint
class ItemScoreBannerFragment : BaseFragment<FragmentScoreBannerBinding>() {
    override val TAG = ItemScoreBannerFragment::class.java.simpleName
    private var position = -1
    private var student: Student? = null

    override fun getDataBinding() = FragmentScoreBannerBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runCatching {
            position = requireArguments().getInt(KEY_POSITION, -1)
            student = jsonStringToObject<Student>(requireArguments().getString(KEY_STUDENT) ?: "")
        }
        if (student == null || position == -1) return

        hideAllLayout()
        when (position) {
            GPA_POS -> {
                binding.layoutGpa.makeVisible()
                binding.pgrGpa.setProgress(100 - ((student!!.avgScore.toFloat() / 4.0f) * 100).toInt())
                binding.tvGpa.text = student!!.avgScore.toString()
                binding.tvName.text = student!!.name
                binding.tvId.text = student!!.id
                binding.tvClass.text = student!!.classInSchool
            }

            PASSED_POS -> {
                binding.layoutPassed.makeVisible()
                binding.pgrPassedSubject.setProgress(100 - ((student!!.passedSubjects.toFloat() / student!!.sizeScores.toFloat()) * 100).toInt())
                binding.tvPassedSubject.text = "${student!!.passedSubjects}/${student!!.sizeScores}"
            }

            FAILED_POS -> {
                binding.layoutFailed.makeVisible()
                binding.pgrFailedSubject.setProgress(100 - ((student!!.failedSubjects.toFloat() / student!!.sizeScores.toFloat()) * 100).toInt())
                binding.tvFailedSubject.text = "${student!!.failedSubjects}/${student!!.sizeScores}"
            }
        }
    }

    private fun hideAllLayout() {
        binding.layoutGpa.makeInVisible()
        binding.layoutPassed.makeInVisible()
        binding.layoutFailed.makeInVisible()
    }

    companion object {
        fun newInstance(position: Int, student: String): ItemScoreBannerFragment {
            return ItemScoreBannerFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_POSITION, position)
                    putString(KEY_STUDENT, student)
                }
            }
        }
    }
}