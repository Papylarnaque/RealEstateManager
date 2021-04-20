package com.openclassrooms.realestatemanager.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.databinding.ItemEstatePicturesBinding

class PictureListAdapter(private val clickListener: PictureListener) :
    ListAdapter<Picture, PictureListAdapter.ViewHolder>(PictureDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemEstatePicturesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Picture, clickListener: PictureListener) {
            binding.picture = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEstatePicturesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class PictureDiffCallback : DiffUtil.ItemCallback<Picture>() {
    override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean {
        return oldItem.pictureId == newItem.pictureId
    }

    override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean {
        return oldItem == newItem
    }
}


class PictureListener(val clickListener: (picture: Picture) -> Unit) {
    fun onClick(picture: Picture) = clickListener(picture)
}