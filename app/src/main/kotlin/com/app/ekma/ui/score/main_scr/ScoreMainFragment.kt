package com.app.ekma.ui.score.main_scr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.app.ekma.activities.test_event.EventHandlingActivity
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.super_utils.click.performClick
import com.app.ekma.common.super_utils.click.setOnSingleClickListener
import com.app.ekma.databinding.FragmentScoreMainBinding
import com.app.ekma.ui.score.search.SearchDataDialogFragment
import com.cuocdat.activityutils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScoreMainFragment : BaseFragment() {
    override val TAG = ScoreMainFragment::class.java.simpleName
    private lateinit var binding: FragmentScoreMainBinding
    private val viewModel by viewModels<ScoreMainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.viewFakeStatus.updateLayoutParams<LinearLayout.LayoutParams> {
            height = getStatusBarHeight
        }
        binding.btnTest.setOnClickListener {
            startActivity(Intent(requireContext(), EventHandlingActivity::class.java))
        }
        binding.btnSearchFeature.setOnSingleClickListener {
            onClickShowSearchDialog()
        }
        binding.scoreMainViewModel = viewModel
    }

    private fun onClickShowSearchDialog() {
        SearchDataDialogFragment().show(
            parentFragmentManager,
            SearchDataDialogFragment::class.java.simpleName
        )
    }
}