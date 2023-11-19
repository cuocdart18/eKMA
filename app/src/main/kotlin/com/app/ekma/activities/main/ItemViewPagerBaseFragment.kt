package com.app.ekma.activities.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.app.ekma.common.FRAGMENT_TYPE
import com.app.ekma.common.INFORMATION_FRAGMENT
import com.app.ekma.common.NOTE_FRAGMENT
import com.app.ekma.common.SCHEDULE_FRAGMENT
import com.app.ekma.common.SCORE_FRAGMENT
import com.app.ekma.databinding.FragmentItemViewpagerBaseBinding
import com.app.ekma.ui.infor.main.InformationFragment
import com.app.ekma.ui.note.main_scr.NoteMainFragment
import com.app.ekma.ui.schedule.main_scr.ScheduleMainFragment
import com.app.ekma.ui.score.main_scr.ScoreMainFragment

class ItemViewPagerBaseFragment : Fragment() {
    private lateinit var binding: FragmentItemViewpagerBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemViewpagerBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (requireArguments().getInt(FRAGMENT_TYPE)) {
            SCORE_FRAGMENT -> {
                childFragmentManager.commit {
                    add<ScoreMainFragment>(binding.fragmentContainerView.id)
                    setReorderingAllowed(true)
                }
            }

            SCHEDULE_FRAGMENT -> {
                childFragmentManager.commit {
                    add<ScheduleMainFragment>(binding.fragmentContainerView.id)
                    setReorderingAllowed(true)
                }
            }

            NOTE_FRAGMENT -> {
                childFragmentManager.commit {
                    add<NoteMainFragment>(binding.fragmentContainerView.id)
                    setReorderingAllowed(true)
                }
            }

            INFORMATION_FRAGMENT -> {
                childFragmentManager.commit {
                    add<InformationFragment>(binding.fragmentContainerView.id)
                    setReorderingAllowed(true)
                }
            }

            else -> {
                childFragmentManager.commit {
                    add<ScoreMainFragment>(binding.fragmentContainerView.id)
                    setReorderingAllowed(true)
                }
            }
        }
    }
}