package com.car2go.viewmodelandnavigationsample.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.car2go.viewmodelandnavigationsample.R
import com.car2go.viewmodelandnavigationsample.viewmodel.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.view.*

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var initialUpdateRan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.setTitle(R.string.settings_screen_title)

        viewModel.start()
        viewModel.state.observe(this) { state ->
            sortDescending.isChecked = state.sortDescending

            if (!initialUpdateRan) {
                initialUpdateRan = true
                sortDescending.jumpDrawablesToCurrentState()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_settings, container, false).apply {
            sortDescending.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setSortDescending(isChecked)
            }
        }
    }
}