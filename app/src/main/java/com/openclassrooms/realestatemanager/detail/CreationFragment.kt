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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.KUtil
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentCreationBinding
import com.openclassrooms.realestatemanager.viewmodel.CreationViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.abs


private const val PERMISSION_CODE = 10001
private const val PICK_IMAGE_TYPE = "image/*"

class CreationFragment : Fragment() {

    // TODO Keep typed data while rotating device

    private lateinit var binding: FragmentCreationBinding
    private lateinit var viewModel: CreationViewModel
    private val args: CreationFragmentArgs by navArgs()
    private var editMode = false
    private var documentUri: Uri? = null
    private var estateCreationOK: Boolean = true
    private var errorMessage: String? = null
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


    override fun onCreate(savedInstanceState: Bundle?) {
        if (args.estateKey != -1L) {
            editMode = true
            (activity as MainActivity).supportActionBar!!.title =
                getString(R.string.edit_estate_titlebar)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_creation, container, false
        )
        // Get the viewModel
        viewModel = ViewModelProvider(this).get(CreationViewModel::class.java)

        initBindings()

        return binding.root
    }


    /**
     * View Bindings
     */
    private fun initBindings() = if (editMode) {
        viewModel.getEstateWithId(args.estateKey).observe(viewLifecycleOwner, {
            if (it != null) {
                binding.estate = it
            }

            // Return Estate Type for edition
            if (it.estateType == resources.getString(R.string.create_estate_flat)) {
                binding.flatButton.isChecked = true
            } else {
                binding.houseButton.isChecked = true
            }

        })

        binding.createEstate.setOnClickListener {
            estateCreationOK = true //init status
            createEstate()
        }

    } else {
        binding.createImage.setOnClickListener {
            // Open gallery for image
            // TODO() Handle permissions
            actionOpenDocument.launch(arrayOf(PICK_IMAGE_TYPE))
            // TODO() Handle case taking a new picture
        }

        binding.createEstate.setOnClickListener {
            estateCreationOK = true //init status
            createEstate()
        }
    }

    /**
     * Handle creation Estate response
     */
    private fun createEstate() {
        val estateCity = getEstateCity()
        val estatePostalCode = getEstatePostalCode()
        val estateStreetNumber = getEstateStreetNumber()
        val estateStreet = getEstateStreet()
        val estateRooms = getEstateRooms()
        val estateSurface = getEstateSurface()
        val estatePrice = getEstatePrice()
        val estateDescription = getEstateDescription()
        val estateType = getEstateType()
        val estateEmployee = getEstateEmployee()

        if (!estateCreationOK) {
            errorMessage?.let { KUtil.infoSnackBar(requireView(), it) }

        } else if (editMode) {
            GlobalScope.launch {
                viewModel.updateEstate(
                    args.estateKey,
                    documentUri,
                    estateType,
                    estateDescription,
                    estatePrice,
                    estateSurface,
                    estateRooms,
                    estateStreet,
                    estateStreetNumber,
                    estatePostalCode,
                    estateCity,
                    estateEmployee
                )
            }
            NavHostFragment.findNavController(this)
                .navigate(
                    CreationFragmentDirections
                        .actionCreationFragmentToDetailFragment(args.estateKey)
                )
        } else {
            GlobalScope.launch {
                viewModel.createNewEstate(
                    documentUri,
                    estateType,
                    estateDescription,
                    estatePrice,
                    estateSurface,
                    estateRooms,
                    estateStreet,
                    estateStreetNumber,
                    estatePostalCode,
                    estateCity,
                    estateEmployee
                )
            }
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_creationFragment_to_listFragment)
        }


    }

//------- GET Estate Data for Creation

    private fun getEstateType(): String {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.flatButton -> {
                resources.getString(R.string.create_estate_flat)
            }
            R.id.houseButton -> {
                resources.getString(R.string.create_estate_house)
            }
            else -> {
                errorMessage = getString(R.string.create_type_error_text)
                estateCreationOK = false
                ""
            }
        }
    }

    private fun getEstateDescription(): String {
        val descriptionMin = resources.getInteger(R.integer.create_description_minimum)
        if (binding.createDescriptionEdit.text.toString().length < descriptionMin) {
            errorMessage =
                getString(
                    R.string.create_description_error_text,
                    descriptionMin.toString()
                )
            estateCreationOK = false
        }
        return binding.createDescriptionEdit.text.toString()
    }

    private fun getEstatePrice(): Int {
        val priceMin = resources.getInteger(R.integer.create_price_minimum)
        binding.createPriceEdit.text.toString().toIntOrNull().let {
            return when (it) {
                null -> {
                    errorMessage = getString(R.string.create_price_missing_error_text)
                    estateCreationOK = false
                    return 0
                }
                in 0..priceMin -> {
                    errorMessage = getString(R.string.create_price_toolow_error_text)
                    estateCreationOK = false
                    it
                }
                else -> {
                    abs(it)
                }
            }
        }
    }

    private fun getEstateSurface(): Int? {
        return if (binding.createSurfaceEdit.text.toString().isBlank()) {
            null
        } else {
            return binding.createSurfaceEdit.text.toString().toInt()
        }
    }

    private fun getEstateRooms(): Int? {
        return if (binding.createRoomsEdit.text.toString().isBlank()) {
            null
        } else {
            binding.createRoomsEdit.text.toString().toInt()
        }
    }

    private fun getEstateStreet(): String {
        return binding.createAddressStreetEdit.text.toString()
    }

    private fun getEstateStreetNumber(): Int? {
        return if (binding.createAddressStreetnumberEdit.text.toString().isBlank()) {
            null
        } else {
            if (getEstateStreet().isBlank()) {
                errorMessage = getString(R.string.create_streetnumber_error_text)
                estateCreationOK = false
            }
            binding.createAddressStreetnumberEdit.text.toString().toInt()
        }
    }

    private fun getEstatePostalCode(): String? {
        return if (binding.createAddressPostalcodeEdit.text.toString().isBlank()) {
            null
        } else {
            binding.createAddressPostalcodeEdit.text.toString()
        }
    }

    private fun getEstateEmployee(): String {
        return binding.createEmployeeEdit.text.toString()
    }

    private fun getEstateCity(): String {
        val createEstateCity = binding.createAddressCityEdit.text.toString()
        if (createEstateCity.isBlank()) { // https://fr.wikipedia.org/wiki/Y_(Somme)
            // Smallest city name in France is 1 letter length
            errorMessage = getString(R.string.create_city_error_text)
            estateCreationOK = false
        }
        return createEstateCity
    }

}
