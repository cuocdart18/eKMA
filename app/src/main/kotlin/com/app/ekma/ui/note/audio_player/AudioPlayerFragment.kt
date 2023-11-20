package com.app.ekma.ui.note.audio_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import com.app.ekma.R
import com.app.ekma.base.fragment.BaseFragment
import com.app.ekma.common.KEY_PASS_VOICE_AUDIO_NAME
import com.app.ekma.common.PAUSE_PLAYING
import com.app.ekma.common.RESUME_PLAYING
import com.app.ekma.common.START_PLAYING
import com.app.ekma.common.formatAudioDuration
import com.app.ekma.databinding.LayoutVoicePlayerBinding
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
        viewModel.checkAudioFileExists(requireContext()) { isExists ->
            if (isExists) {
                viewModel.initMediaPlayer(state, updateDuration)
                setupView()
                binding.tvAudioFileIsNotExists.visibility = View.GONE
                binding.layoutAudioController.visibility = View.VISIBLE
            } else {
                binding.tvAudioFileIsNotExists.visibility = View.VISIBLE
                binding.layoutAudioController.visibility = View.GONE
            }
        }
    }

    private fun receiveNote() {
        viewModel.audioName = requireArguments().getString(KEY_PASS_VOICE_AUDIO_NAME, "")
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

    private val state: (state: Int) -> Unit = {
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