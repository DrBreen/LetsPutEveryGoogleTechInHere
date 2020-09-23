package com.car2go.viewmodelandnavigationsample.view.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.car2go.viewmodelandnavigationsample.R
import com.car2go.viewmodelandnavigationsample.model.Place
import kotlinx.android.synthetic.main.item_place.view.*

class FavoritePlacesAdapter: ListAdapter<Place, FavoritePlacesAdapter.ViewHolder>(
    FavoritePlacesAdapter
) {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.placeName
        val placeCoordinates: TextView = view.placeCoordinates
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            with(holder) {
                placeCoordinates.text = "${it.latitude} ${it.longitude}"
                placeName.text = it.name
            }
        }
    }

    private companion object: DiffUtil.ItemCallback<Place>() {

        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }

    }
}

