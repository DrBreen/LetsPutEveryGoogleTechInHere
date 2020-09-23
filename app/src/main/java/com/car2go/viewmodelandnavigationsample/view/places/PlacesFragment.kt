package com.car2go.viewmodelandnavigationsample.view.places

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.car2go.viewmodelandnavigationsample.R
import com.car2go.viewmodelandnavigationsample.viewmodel.places.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_places.*
import kotlinx.android.synthetic.main.activity_places.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class PlacesFragment: Fragment() {

    private val placesViewModel: PlacesViewModel by viewModels()

    private var editButton: MenuItem? = null
    private var cancelButton: MenuItem? = null
    private var settingsButton: MenuItem? = null

    private val adapter = FavoritePlacesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_places, container, false).apply {
            placesRecyclerView.adapter = adapter

            placeLatitudeEditText.doAfterTextChanged {
                placesViewModel.setLatitude(it?.toString()?.toDoubleOrNull())
            }

            placeLongitudeEditText.doAfterTextChanged {
                placesViewModel.setLongitude(it?.toString()?.toDoubleOrNull())
            }

            placeNameEditText.doAfterTextChanged {
                it?.toString()?.let {
                    placesViewModel.setName(it)
                }
            }

            saveButton.setOnClickListener {
                placesViewModel.addNewPlace()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.string.main_screen_title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        placesViewModel.start()
        placesViewModel.state.observe(this) { state ->

            val transition = Slide(Gravity.BOTTOM)
            transition.addTarget(R.id.editorContainer)
            TransitionManager.beginDelayedTransition(root, transition)
            editorContainer.isVisible = state.editing

            editButton?.isVisible = !state.editing
            cancelButton?.isVisible = state.editing

            saveButton.isEnabled = state.canSave
            adapter.submitList(state.places)
            // TODO: clear texts!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_screen, menu)

        editButton = menu.findItem(R.id.startEditing)
        cancelButton = menu.findItem(R.id.cancel)
        settingsButton = menu.findItem(R.id.settings)

        editButton!!.setOnMenuItemClickListener {
            placesViewModel.startEditing()
            true
        }

        cancelButton!!.setOnMenuItemClickListener {
            placesViewModel.cancelEditing()
            true
        }

        settingsButton!!.setOnMenuItemClickListener {
            // TODO: use better animation!
            findNavController().navigate(R.id.action_placesFragment_to_settingsFragment)
            true
        }
    }

}