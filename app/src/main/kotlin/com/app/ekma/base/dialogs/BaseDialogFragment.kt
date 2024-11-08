package com.app.ekma.base.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.app.ekma.R
import com.app.ekma.common.SCALE_LAYOUT_SEARCH_DATA_DIALOG_X
import com.app.ekma.common.SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y

abstract class BaseDialogFragment<T : ViewBinding> : DialogFragment() {
    protected open val TAG = ""

    private var _binding: T? = null
    protected val binding: T
        get() = _binding as T

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
        // set theme for dialog
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnim
        _binding = getDataBinding()
        return binding.root
    }

    abstract fun getDataBinding(): T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifecycle("onViewCreated")
        // setup UI
        setScaleUI()
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
        Log.d(TAG, "$msg $TAG")
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

    protected fun navigateToFragment(id: Int) {
        findNavController().navigate(id)
    }

    protected fun navigateToFragment(id: Int, data: Bundle) {
        findNavController().navigate(id, data)
    }

    // FUN
    private fun setScaleUI() {
        logDebug("setting scale UI")
        val window = dialog?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.getSize(size)
        window?.setLayout(
            ((size.x * SCALE_LAYOUT_SEARCH_DATA_DIALOG_X).toInt()),
            ((size.y * SCALE_LAYOUT_SEARCH_DATA_DIALOG_Y).toInt())
        )
        window?.setGravity(Gravity.CENTER)
    }

    internal fun showToast(msg: String, type: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), msg, type).show()
    }
}