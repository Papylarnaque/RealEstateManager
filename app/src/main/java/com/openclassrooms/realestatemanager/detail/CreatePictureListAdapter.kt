package com.openclassrooms.realestatemanager.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.databinding.ItemCreateEstatePicturesBinding

class CreatePictureListAdapter(private val clickListenerCreate: CreatePictureListener) :
    ListAdapter<Picture, CreatePictureListAdapter.ViewHolder>(PictureDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListenerCreate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemCreateEstatePicturesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Picture, clickListenerCreate: CreatePictureListener) {
            binding.picture = item
            binding.clickListener = clickListenerCreate
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCreateEstatePicturesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CreatePictureListener(val clickListener: (picture: Picture) -> Unit) {
    fun onClick(picture: Picture)
            = clickListener(picture)

    fun delete(picture: Picture)
            = clickListener(picture)
}