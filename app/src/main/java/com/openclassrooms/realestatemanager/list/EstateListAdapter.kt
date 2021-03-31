package com.openclassrooms.realestatemanager.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.databinding.ItemEstateBinding


class EstateListAdapter(private val onClick: (Estate) -> Unit) :
        ListAdapter<Estate, EstateListAdapter.EstateViewHolder>(EstateDiffCallback) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class EstateViewHolder(itemView: View, val onClick: (Estate) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
        // TODO() Handle binding instead of findViewById

        private lateinit var binding: ItemEstateBinding

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

            binding.itemEstateFirstLine.text = estate.estateType
            binding.itemEstateSecondLine.text = estate.estateCity
            binding.itemEstateThirdLine.text = estate.estatePrice.toString()
            Glide.with(itemView)
                    .load(estate.pictureUrl)
                    .thumbnail(0.33f)
                    .centerCrop()
                    .into(binding.itemEstatePicture)

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

