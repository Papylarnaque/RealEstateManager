package com.openclassrooms.realestatemanager.creation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentCreationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val PERMISSION_CODE = 10001

class CreationFragment : Fragment() {

    private lateinit var binding: FragmentCreationBinding
    private lateinit var viewModel: CreationViewModel

    private var documentUri: Uri? = null

    // TODO() Return back to list if we cancel creation


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //  Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_creation,
                container,
                false
        )

        // BackPress handling
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Disabled
        }

        initBindings()

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(CreationViewModel::class.java)

        return binding.root
    }

    /**
     * View Bindings
     */
    private fun initBindings() {

        binding.createImage.setOnClickListener {
            // Open gallery for image
            // TODO() Handle permissions
            actionOpenDocument.launch(arrayOf("image/*"))

            // TODO() Handle case taking a new picture
        }


        binding.createTypeFirstCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            checkBoxClick(
                    buttonView,
                    isChecked
            )
        }
        binding.createTypeSecondCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            checkBoxClick(
                    buttonView,
                    isChecked
            )
        }

        binding.createEstate.setOnClickListener {

            GlobalScope.launch {
                viewModel.createNewEstate(
                        documentUri,
                        binding.createTypeFirstCheckBox.isChecked,
                        binding.createTypeSecondCheckBox.isChecked
                )
            }

            // TODO() Handle landscape case
            //      (click on create should open Detail View with the list on the left)
            if (activity?.resources?.getBoolean(R.bool.is_landscape) == true) {
                NavHostFragment
                        .findNavController(this)
                        .navigate(R.id.listFragment)
            } else {
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_creationFragment_to_detailFragment)
            }

        }
    }

    // TODO() update checkbox logic
    //        move code to VM
    // CheckBox clicks logic
    private fun checkBoxClick(buttonView: CompoundButton, isChecked: Boolean) {
        //get item id to handle item clicks
        val id = buttonView.id

        //handle item clicks
        if (id == binding.createTypeFirstCheckBox.id) {
            //Open CreationFragment
            Log.i("Creation Fragment", "first checkbox click")
            binding.createTypeFirstCheckBox.isChecked = isChecked
            binding.createTypeSecondCheckBox.isChecked != isChecked

        } else if (id == binding.createTypeSecondCheckBox.id) {
            Log.i("Creation Fragment", "second checkbox click")
            binding.createTypeFirstCheckBox.isChecked != isChecked
            binding.createTypeSecondCheckBox.isChecked = isChecked

        } else Log.e("Creation Fragment", "CheckBox click issue: id = $id")
    }


    private val actionOpenDocument =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                // If the user returns to this fragment without selecting a file, uri will be null
                // In this case, we return void
                documentUri = uri ?: return@registerForActivityResult

                // TODO() Store the image in app folder instead of external link

                Log.i("Creation Fragment", "New picture added, uri: $documentUri")

                Glide.with(requireView())
                        .load(documentUri)
                        .thumbnail(0.33f)
                        .centerCrop()
                        .into(binding.createImage)


            }

}