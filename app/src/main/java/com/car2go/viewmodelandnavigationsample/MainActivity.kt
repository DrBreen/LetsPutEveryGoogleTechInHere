package com.car2go.viewmodelandnavigationsample

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.car2go.viewmodelandnavigationsample.view.main.FavoritePlacesAdapter
import com.car2go.viewmodelandnavigationsample.viewmodel.places.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private val placesViewModel: PlacesViewModel by viewModels()

    private var editButton: MenuItem? = null
    private var cancelButton: MenuItem? = null

    private val adapter = FavoritePlacesAdapter()

    // TODO: navigation?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setTitle(R.string.main_screen_title)

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

        placesViewModel.start(this)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_screen, menu)

        editButton = menu.findItem(R.id.startEditing)
        cancelButton = menu.findItem(R.id.cancel)

        editButton!!.setOnMenuItemClickListener {
            placesViewModel.startEditing()
            true
        }

        cancelButton!!.setOnMenuItemClickListener {
            placesViewModel.cancelEditing()
            true
        }

        return true
    }

}