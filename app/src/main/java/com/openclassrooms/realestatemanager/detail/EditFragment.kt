package com.openclassrooms.realestatemanager.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.viewmodel.CreationViewModel

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private lateinit var viewModel: CreationViewModel
    private var documentUri: Uri? = null
    private val actionOpenDocument =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                // If the user returns to this fragment without selecting a file, uri will be null
                // In this case, we return void
                documentUri = uri ?: return@registerForActivityResult

                // TODO() Store the image in app folder instead of external link

                Log.i("Creation Fragment", "New picture added, uri: $documentUri")

//                Glide.with(requireView())
//                        .load(documentUri)
//                        .thumbnail(0.33f)
//                        .centerCrop()
//                        .into(binding.createImage)


            }

    // TODO() Return back to list if we cancel creation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //  Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail,
                container, false)

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(CreationViewModel::class.java)

        return binding.root
    }

}