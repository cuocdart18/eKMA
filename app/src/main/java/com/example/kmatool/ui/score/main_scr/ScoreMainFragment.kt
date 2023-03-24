package com.example.kmatool.ui.score.main_scr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kmatool.databinding.FragmentScoreMainBinding
import androidx.lifecycle.ViewModelProvider
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.utils.KIT_URL

class ScoreMainFragment : BaseFragment() {
    override val TAG = ScoreMainFragment::class.java.simpleName
    private lateinit var binding: FragmentScoreMainBinding
    private val scoreMainViewModel: ScoreMainViewModel by lazy {
        ViewModelProvider(requireActivity())[ScoreMainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreMainBinding.inflate(inflater, container, false)
        // mapping to UI
        binding.btnSearchFeature.setOnClickListener { onClickShowSearchDialog() }
        binding.scoreMainViewModel = scoreMainViewModel

        return binding.root
    }

    private fun onClickShowSearchDialog() {
        logDebug("onClickShowSearchDialog")
        // action (open dialog)
        navigateToFragment(R.id.searchDataDialogFragment)
    }

    private fun onClickTagFooter() {
        logDebug("onClickTagFooter")
        // action
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(KIT_URL)
        startActivity(intent)
    }
}