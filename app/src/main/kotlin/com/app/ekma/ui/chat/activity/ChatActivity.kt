package com.app.ekma.ui.chat.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.app.ekma.R
import com.app.ekma.base.activities.BaseActivity
import com.app.ekma.databinding.ActivityChatBinding
import com.app.ekma.ui.chat.list.ListChatFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    override val TAG = ChatActivity::class.java.simpleName
    private lateinit var binding: ActivityChatBinding
    private val viewModel by viewModels<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        supportFragmentManager.commit {
            replace<ListChatFragment>(R.id.frmContainer)
            setReorderingAllowed(true)
//            addToBackStack(ListChatFragment::class.java.simpleName)
        }
    }
}