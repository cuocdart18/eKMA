package com.app.ekma.ui.score.main_scr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.databinding.FragmentScoreMainBinding
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
        binding.btnSearchFeature.setOnClickListener { onClickShowSearchDialog() }
        binding.scoreMainViewModel = viewModel
    }

    private fun onClickShowSearchDialog() {
        logDebug("onClickShowSearchDialog")
        navigateToFragment(R.id.searchDataDialogFragment)
    }
}