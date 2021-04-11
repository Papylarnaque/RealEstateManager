package com.openclassrooms.realestatemanager.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.databinding.ItemEstateBinding


class EstateListAdapter(private val clickListener: EstateListener) : ListAdapter<Estate, EstateListAdapter.ViewHolder>(EstateDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemEstateBinding) : RecyclerView.ViewHolder(binding.root) {

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

class EstateListener(val clickListener: (estate: Estate) -> Unit) {
    fun onClick(estate: Estate) = clickListener(estate)
}


@BindingAdapter("pictureUrl")
fun loadImage(view: ImageView, url: String) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

    Glide.with(view)
            .load(url)
            .transform(RoundedCorners(15))
            .into(view)
}