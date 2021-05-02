package com.openclassrooms.realestatemanager.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.databinding.ItemCreateEstatePicturesBinding

class CreatePictureListAdapter(
    private val clickListenerCreate: CreatePictureListener,
    private val deleteListenerCreate: DeletePictureListener
) :
    ListAdapter<Picture, CreatePictureListAdapter.ViewHolder>(PictureDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListenerCreate, deleteListenerCreate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemCreateEstatePicturesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Picture, clickListenerCreate: CreatePictureListener, deleteListenerCreate: DeletePictureListener) {
            binding.picture = item
            binding.clickListener = clickListenerCreate
            binding.deleteListener = deleteListenerCreate
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
    fun onClick(picture: Picture) = clickListener(picture)
}

class DeletePictureListener(val deleteListener: (picture: Picture) -> Unit) {
    fun delete(picture: Picture) = deleteListener(picture)
}
