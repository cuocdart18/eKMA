package com.app.ekma.base.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.app.ekma.R
import com.app.ekma.common.makeVisible
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import java.time.LocalDate
import java.time.LocalTime

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    protected open val TAG = ""

    private var _binding: T? = null
    protected val binding: T
        get() = _binding as T

    abstract fun getDataBinding(): T
    open fun initDataByArgs() {}
    open fun initViewModel() {}
    open fun initView() {}
    open fun addEvent() {}
    open fun addObservers() {}
    open fun initData() {}

    // LIFECYCLE
    override fun onAttach(context: Context) {
        super.onAttach(context)
        logLifecycle("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logLifecycle("onCreateView")
        _binding = getDataBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated")
        initDataByArgs()
        initViewModel()
        initView()
        addEvent()
        addObservers()
        initData()
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logLifecycle("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logLifecycle("onDetach")
    }

    // LOG
    open fun logLifecycle(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logError(msg: String) {
        Log.e(TAG, msg)
    }

    open fun logDebug(msg: String) {
        Log.d(TAG, msg)
    }

    open fun logInfo(msg: String) {
        Log.i(TAG, msg)
    }

    open fun logWarning(msg: String) {
        Log.w(TAG, msg)
    }

    // FUN
    protected fun navigateToFragment(id: Int) {
        findNavController().navigate(id)
    }

    protected fun popBackstack(id: Int, inclusive: Boolean) {
        findNavController().popBackStack(id, inclusive)
    }

    protected fun navigateToFragment(id: Int, data: Bundle) {
        findNavController().navigate(id, data)
    }

    internal fun openDatePickerDialog(
        callback: (view: DatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
    ) {
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().month.value - 1
        val currentDayOfMonth = LocalDate.now().dayOfMonth
        DatePickerDialog(
            requireContext(),
            R.style.CustomPickerDialogTheme,
            { view, year, month, dayOfMonth ->
                logInfo("pick day=$dayOfMonth - month=${month + 1} - year=$year")
                callback(view, year, month + 1, dayOfMonth)
            },
            currentYear,
            currentMonth,
            currentDayOfMonth
        ).apply {
            window?.attributes?.windowAnimations = R.style.DialogAnim
            show()
        }
    }

    internal fun openTimePickerDialog(
        callback: (view: TimePicker, hourOfDay: Int, minute: Int) -> Unit
    ) {
        val currentHour = LocalTime.now().hour
        val currentMinute = LocalTime.now().minute
        TimePickerDialog(
            requireContext(),
            R.style.CustomPickerDialogTheme,
            { view, hourOfDay, minute ->
                logInfo("pick hour=$hourOfDay - minute=$minute")
                callback(view, hourOfDay, minute)
            },
            currentHour,
            currentMinute,
            true
        ).apply {
            window?.attributes?.windowAnimations = R.style.DialogAnim
            show()
        }
    }

    internal fun showAlertDialog(
        bgrId: Int,
        title: String,
        message: String,
        textBtnConfirm: String,
        textBtnRefuse: String,
        onClickConfirm: () -> Unit,
        onClickRefuse: () -> Unit,
        isAlert: Boolean
    ): Dialog {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_custom_alert_yes_or_no)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnim
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imvPresentation = dialog.findViewById<ImageView>(R.id.imv_present_action)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_dialog_title)
        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnYes = dialog.findViewById<Button>(R.id.btn_yes)
        val btnNo = dialog.findViewById<Button>(R.id.btn_no)

        imvPresentation.setBackgroundResource(bgrId)
        tvTitle.text = title
        tvMessage.text = message
        btnYes.text = textBtnConfirm
        btnNo.text = textBtnRefuse
        if (isAlert) {
            btnNo.visibility = View.GONE
        }
        btnYes.setOnSingleClickListener {
            onClickConfirm()
        }
        btnNo.setOnSingleClickListener {
            onClickRefuse()
        }
        return dialog
    }

    internal fun setupGoogleProgress(progressBar: ProgressBar) {
        progressBar.indeterminateDrawable =
            ChromeFloatingCirclesDrawable.Builder(requireContext())
                .colors(resources.getIntArray(R.array.google_colors))
                .build()
        progressBar.makeVisible()
    }

    internal fun showToast(msg: String, type: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), msg, type).show()
    }
}