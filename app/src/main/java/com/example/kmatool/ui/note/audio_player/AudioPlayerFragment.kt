package com.example.kmatool.ui.note.audio_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.example.kmatool.R
import com.example.kmatool.base.fragment.BaseFragment
import com.example.kmatool.common.KEY_PASS_VOICE_AUDIO_PATH
import com.example.kmatool.common.PAUSE_PLAYING
import com.example.kmatool.common.RESUME_PLAYING
import com.example.kmatool.common.START_PLAYING
import com.example.kmatool.common.formatAudioDuration
import com.example.kmatool.databinding.LayoutVoicePlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioPlayerFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener {
    override val TAG = AudioPlayerFragment::class.java.simpleName
    private lateinit var binding: LayoutVoicePlayerBinding
    private val viewModel by viewModels<AudioPlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutVoicePlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveNote()
        initMediaPlayer()
        setupView()
    }

    private fun receiveNote() {
        viewModel.audioPath = requireArguments().getString(KEY_PASS_VOICE_AUDIO_PATH, "")
    }

    private fun initMediaPlayer() {
        viewModel.initMediaPlayer(uiCallBack, updateDuration)
    }

    private fun setupView() {
        binding.tvCurrentDuration.text =
            formatAudioDuration(viewModel.getAudioCurrentDuration())
        binding.tvDuration.text = formatAudioDuration(viewModel.getAudioDuration())
        binding.sbAudioPlayer.max = viewModel.getAudioDuration()
        binding.sbAudioPlayer.progress = viewModel.getAudioCurrentDuration()

        binding.btnPlayPause.setOnClickListener { viewModel.onClickBtnPlayPause() }
        binding.btnJumpPlus.setOnClickListener { onClickBtnJump(5000) }
        binding.btnJumpMinus.setOnClickListener { onClickBtnJump(-5000) }
        binding.sbAudioPlayer.setOnSeekBarChangeListener(this)
    }

    private val uiCallBack: (state: Int) -> Unit = {
        when (it) {
            RESUME_PLAYING -> {
                binding.btnPlayPause.setImageResource(R.drawable.pause_circle_outline_red)
            }

            PAUSE_PLAYING -> {
                binding.btnPlayPause.setImageResource(R.drawable.play_circle_outline_red)
            }

            START_PLAYING -> {
                binding.btnPlayPause.setImageResource(R.drawable.pause_circle_outline_red)
            }
        }
    }

    private val updateDuration: (duration: Int) -> Unit = {
        binding.sbAudioPlayer.progress = it
        binding.tvCurrentDuration.text = formatAudioDuration(it)
    }

    private fun onClickBtnJump(space: Int) {
        val newProgress = binding.sbAudioPlayer.progress + space
        binding.sbAudioPlayer.progress = newProgress
        binding.tvCurrentDuration.text = formatAudioDuration(binding.sbAudioPlayer.progress)
        viewModel.seekToByUser(binding.sbAudioPlayer.progress)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            viewModel.seekToByUser(progress)
            binding.tvCurrentDuration.text = formatAudioDuration(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onStop() {
        super.onStop()
        viewModel.onPausePlayer()
    }
}