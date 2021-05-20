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
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.databinding.ItemEstateBinding
import com.openclassrooms.realestatemanager.utils.formatPrice


class EstateListAdapter(private val clickListener: EstateListener) :
    ListAdapter<DetailedEstate, EstateListAdapter.ViewHolder>(EstateDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemEstateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetailedEstate, clickListener: EstateListener) {
            binding.it = item
            if (!item.pictures?.isEmpty()!!) {
                loadImage(binding.itemEstatePicture, item.pictures!![0].url)
            }
            binding.clickListener = clickListener
            binding.itemEstateThirdLine.text = item.estate?.estatePrice?.let { formatPrice(it) + "$"}
            // if SOLD
            if (item.estate?.endTime != null) {
                binding.detailSoldTag.visibility = View.VISIBLE
                binding.itemEstateThirdLine.visibility = View.INVISIBLE
            }
//            binding.executePendingBindings()
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

class EstateDiffCallback : DiffUtil.ItemCallback<DetailedEstate>() {
    override fun areItemsTheSame(oldItem: DetailedEstate, newItem: DetailedEstate): Boolean {
        return oldItem.estate?.startTime == newItem.estate?.startTime
    }

    override fun areContentsTheSame(oldItem: DetailedEstate, newItem: DetailedEstate): Boolean {
        return oldItem.estate == newItem.estate && oldItem.pictures == newItem.pictures
    }
}

class EstateListener(val clickListener: (detailedEstate: DetailedEstate) -> Unit) {
    fun onClick(detailedEstate: DetailedEstate) = clickListener(detailedEstate)
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