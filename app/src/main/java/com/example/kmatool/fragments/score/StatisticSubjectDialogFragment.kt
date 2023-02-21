package com.example.kmatool.fragments.score

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.kmatool.databinding.DialogStatisticSubjectBinding
import com.example.kmatool.models.score.StatisticSubject
import com.example.kmatool.utils.KEY_PASS_STATISTIC_SUBJECT
import com.example.kmatool.utils.SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_X
import com.example.kmatool.utils.SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_Y

class StatisticSubjectDialogFragment : DialogFragment() {
    private val TAG = StatisticSubjectDialogFragment::class.java.simpleName
    private lateinit var binding: DialogStatisticSubjectBinding
    private lateinit var statisticSubject: StatisticSubject

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "on create view $TAG")
        binding = DialogStatisticSubjectBinding.inflate(inflater, container, false)
        // set theme for dialog
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "show $TAG")
        // setup UI
        setScaleUI()

        // receive data from main fragment
        receiveDataFromStudentDetailFragment()
        // show data to UI
        showStatisticSubjectDataToUI()
    }

    private fun setScaleUI() {
        Log.d(TAG, "setting scale UI")
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.getSize(size)
        window?.setLayout(
            ((size.x * SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_X).toInt()),
            ((size.y * SCALE_LAYOUT_STATISTIC_SUBJECT_DIALOG_Y).toInt())
        )
        window?.setGravity(Gravity.CENTER)
    }

    private fun receiveDataFromStudentDetailFragment() {
        val bundle = arguments
        bundle?.let {
            statisticSubject =
                it.get(KEY_PASS_STATISTIC_SUBJECT) as StatisticSubject
        }
        Log.i(TAG, "receive statistic subject = $statisticSubject")
    }

    private fun showStatisticSubjectDataToUI() {
        Log.d(TAG, "show data to UI")
        // action
        binding.statisticSubject = statisticSubject
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destroy $TAG")
    }
}