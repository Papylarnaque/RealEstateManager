package com.openclassrooms.realestatemanager.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.databinding.ItemEstateBinding


class EstateListAdapter(private val clickListener: EstateListener) : ListAdapter<Estate, EstateListAdapter.ViewHolder>(EstateDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemEstateBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Estate, clickListener: EstateListener) {
            binding.estate = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEstateBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class EstateDiffCallback : DiffUtil.ItemCallback<Estate>() {
    override fun areItemsTheSame(oldItem: Estate, newItem: Estate): Boolean {
        return oldItem.startTimeMilli == newItem.startTimeMilli
    }

    override fun areContentsTheSame(oldItem: Estate, newItem: Estate): Boolean {
        return oldItem == newItem
    }
}

class EstateListener(val clickListener: (estateId: Long) -> Unit) {
    fun onClick(estate: Estate) = clickListener(estate.startTimeMilli)
}


@BindingAdapter("pictureUrl")
fun loadImage(view: ImageView, url: String){
    Glide.with(view)
        .load(url)
        .into(view)
}