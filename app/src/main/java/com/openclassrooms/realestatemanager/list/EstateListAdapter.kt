package com.openclassrooms.realestatemanager.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.EstateAllPictures
import com.openclassrooms.realestatemanager.databinding.ItemEstateBinding


class EstateListAdapter(private val clickListener: EstateListener) :
    ListAdapter<EstateAllPictures, EstateListAdapter.ViewHolder>(EstateDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemEstateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EstateAllPictures, clickListener: EstateListener) {
            binding.estate = item.estate
            if (!item.pictures?.isEmpty()!!){
            loadImage(binding.itemEstatePicture, item.pictures!![0].url)}
            binding.clickListener = clickListener
            // if SOLD
            if (item.estate?.endTime != null) {
                binding.estateSoldTag.visibility = View.VISIBLE
                binding.itemEstateThirdLine.visibility = View.INVISIBLE
            }

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

class EstateDiffCallback : DiffUtil.ItemCallback<EstateAllPictures>() {
    override fun areItemsTheSame(oldItem: EstateAllPictures, newItem: EstateAllPictures): Boolean {
        return oldItem.estate?.startTime == newItem.estate?.startTime
    }

    override fun areContentsTheSame(oldItem: EstateAllPictures, newItem: EstateAllPictures): Boolean {
        return oldItem.estate == newItem.estate && oldItem.pictures == newItem.pictures
    }
}

class EstateListener(val clickListener: (estate: Estate) -> Unit) {
    fun onClick(estate: Estate) = clickListener(estate)
}


@BindingAdapter("pictureUrl")
fun loadImage(view: ImageView, url: String?) {

    if (url != null) {
        Glide.with(view)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(view)
    }
}