package com.example.kmatool.base.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kmatool.R
import com.example.kmatool.common.makeVisible
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar
import java.time.LocalDate
import java.time.LocalTime

open class BaseFragment : Fragment() {
    protected open val TAG = ""

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
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated")
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

    @RequiresApi(Build.VERSION_CODES.N)
    internal fun openDatePickerDialog(
        callback: (view: DatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
    ) {
        val currentYear = LocalDate.now().year
        val currentMonth = LocalDate.now().month.value - 1
        val currentDayOfMonth = LocalDate.now().dayOfMonth
        DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth ->
                logInfo("pick day=$dayOfMonth - month=${month + 1} - year=$year")
                callback(view, year, month + 1, dayOfMonth)
            },
            currentYear,
            currentMonth,
            currentDayOfMonth
        ).show()
    }

    internal fun openTimePickerDialog(
        callback: (view: TimePicker, hourOfDay: Int, minute: Int) -> Unit
    ) {
        val currentHour = LocalTime.now().hour
        val currentMinute = LocalTime.now().minute
        TimePickerDialog(
            requireContext(),
            { view, hourOfDay, minute ->
                logInfo("pick hour=$hourOfDay - minute=$minute")
                callback(view, hourOfDay, minute)
            },
            currentHour,
            currentMinute,
            true
        ).show()
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