package com.openclassrooms.realestatemanager.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.Estate



class EstateListAdapter(private val onClick: (Estate) -> Unit) :
        ListAdapter<Estate, EstateListAdapter.EstateViewHolder>(EstateDiffCallback) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class EstateViewHolder(itemView: View, val onClick: (Estate) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        // TODO() Handle binding instead of findViewById

        private val estateListFirstLine: TextView = itemView.findViewById(R.id.item_estate_first_line)
        private val estateListSecondLine: TextView = itemView.findViewById(R.id.item_estate_second_line)
        private val estateListThirdLine: TextView = itemView.findViewById(R.id.item_estate_third_line)
        private val estateListImageView: ImageView = itemView.findViewById(R.id.item_estate_picture)
        private var currentEstate: Estate? = null

        init {
            itemView.setOnClickListener {
                currentEstate?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(estate: Estate) {
            currentEstate = estate

            estateListFirstLine.text = estate.estateType
            estateListSecondLine.text = estate.estateCity
            estateListThirdLine.text = estate.estatePrice.toString()
            Glide.with(itemView)
                    .load(estate.pictureUrl)
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(estateListImageView)

        }
    }

    /* Creates and inflates view and return EstateViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstateViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_estate, parent, false)
        return EstateViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: EstateViewHolder, position: Int) {
        val estate = getItem(position)
        holder.bind(estate)

    }
}

object EstateDiffCallback : DiffUtil.ItemCallback<Estate>() {
    override fun areItemsTheSame(oldItem: Estate, newItem: Estate): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Estate, newItem: Estate): Boolean {
        return oldItem.startTimeMilli == newItem.startTimeMilli
    }
}

