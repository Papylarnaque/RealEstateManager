package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.KUtil
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.databinding.FragmentCreationBinding
import com.openclassrooms.realestatemanager.utils.GetContentWithMimeTypes
import com.openclassrooms.realestatemanager.viewmodel.CreationViewModel
import kotlin.math.abs


val IMAGE_MIME_TYPE = arrayOf("image/jpeg", "image/png")

class CreationFragment : Fragment() {

    // TODO Keep typed data while rotating device

    private lateinit var binding: FragmentCreationBinding
    private lateinit var viewModel: CreationViewModel
    private val args: CreationFragmentArgs by navArgs()
    private var editMode = false
    private var errorMessage: String? = null
    private var imageUrl: String? = null
    private lateinit var listPicture: List<Picture>
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            viewModel.saveImageFromCamera(bitmap)
        }

    private val selectPicture =
        registerForActivityResult(GetContentWithMimeTypes()) { uri ->
            uri?.let {
                viewModel.copyImageFromUriToAppFolder(uri)
            }
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

        if (editMode)
            editModeBinding()

        val pictureListAdapter = PictureListAdapter(PictureListener { picture ->
            // TODO() viewModel.onPictureClicked(picture)
        })
        binding.createRecyclerviewPictures.adapter = pictureListAdapter
        binding.createRecyclerviewPictures.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)

        if (editMode) {
            viewModel.getEstatePictures(args.estateKey).observe(viewLifecycleOwner, {
                it?.let {
                    pictureListAdapter.submitList(it as MutableList<Picture>)
                }
            })
        }

        initBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateAfterSaveClick()
        super.onViewCreated(view, savedInstanceState)
    }


    //------------------- MANAGE VIEW ----------------------//

    /**
     * EDIT MODE specific bindings
     */
    private fun editModeBinding() {
        viewModel.getEstateWithId(args.estateKey).observe(viewLifecycleOwner, {
            if (it != null) {
                binding.estate = it
            }
            binding.editEstateAvailability.visibility = View.VISIBLE
        })

    }

    /**
     * Global View Bindings
     */
    private fun initBindings() {
        binding.createTakePicture.setOnClickListener {
            // Open camera for image
            takePicture.launch(null)
            // TODO() NavigateUp from Camera navigates back to the creationFragment
            // TODO() Import image from gallery
        }

        binding.createPickGallery.setOnClickListener {
            selectPicture.launch(IMAGE_MIME_TYPE)
        }

        binding.createEstate.setOnClickListener {
            val estate = shareEstate()
            if (!errorMessage.isNullOrEmpty()) {
                errorMessage?.let { KUtil.infoSnackBar(requireView(), it) }
                errorMessage = null
            } else {
                viewModel.saveEstate(editMode, estate)
            }
        }
    }


    //-------------------- NAVIGATION ----------------------//

    private fun navigateAfterSaveClick() {
        // When an item is clicked.
        viewModel.navigateToEstateDetail.observe(viewLifecycleOwner, { estate ->
            estate?.let {
                NavHostFragment.findNavController(this).navigateUp()
            } ?: NavHostFragment.findNavController(this)
                .navigate(R.id.action_creationFragment_to_listFragment)

        })
    }


    //----------------- MANAGE ESTATE ---------------------//
    /**
     * Handle Estate data
     */
    private fun shareEstate(): Estate {
        if (editMode) {
            return Estate(
                startTime = args.estateKey,
                estateCity = getEstateCity(),
                estateCityPostalCode = getEstatePostalCode(),
                estateStreetNumber = getEstateStreetNumber(),
                estateStreet = getEstateStreet(),
                estateRooms = getEstateRooms(),
                estateSurface = getEstateSurface(),
                estatePrice = getEstatePrice(),
                estateDescription = getEstateDescription(),
//            estateType = getEstateType(),
                estateType = "House",
//                estateEmployee = getEstateEmployee(),
                employeeId = 1,
                endTime = getEstateAvailability(),
                estatePois = "Test",
            )
        } else return Estate(
            estateCity = getEstateCity(),
            estateCityPostalCode = getEstatePostalCode(),
            estateStreetNumber = getEstateStreetNumber(),
            estateStreet = getEstateStreet(),
            estateRooms = getEstateRooms(),
            estateSurface = getEstateSurface(),
            estatePrice = getEstatePrice(),
            estateDescription = getEstateDescription(),
//            estateType = getEstateType(),
            estateType = "House",
//                estateEmployee = getEstateEmployee(),
            employeeId = 1,
            endTime = getEstateAvailability(),
            estatePois = "Test"
        )
    }


//------- GET Estate Data for Creation

//    private fun getEstateType(): String {
//        // TODO() Manage more Estate Types => Dropmenu ?
//        return when (binding.radioGroup.checkedRadioButtonId) {
//            R.id.flatButton -> {
//                resources.getString(R.string.create_estate_flat)
//            }
//            R.id.houseButton -> {
//                resources.getString(R.string.create_estate_house)
//            }
//            else -> {
//                errorMessage = getString(R.string.create_type_error_text)
//                ""
//            }
//        }
//    }

    private fun getEstateDescription(): String {
        val descriptionMin = resources.getInteger(R.integer.create_description_minimum)
        if (binding.createDescriptionEdit.text.toString().length < descriptionMin) {
            errorMessage =
                getString(
                    R.string.create_description_error_text,
                    descriptionMin.toString()
                )
        }
        return binding.createDescriptionEdit.text.toString()
    }

    private fun getEstatePrice(): Int {
        val priceMin = resources.getInteger(R.integer.create_price_minimum)
        binding.createPriceEdit.text.toString().toIntOrNull().let {
            return when (it) {
                null -> {
                    errorMessage = getString(R.string.create_price_missing_error_text)
                    return 0
                }
                in 0..priceMin -> {
                    errorMessage = getString(R.string.create_price_toolow_error_text)
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
        return if (binding.createAddressStreetnumberEdit.text.toString().isEmpty()) {
            null
        } else {
            if (getEstateStreet().isEmpty()) {
                errorMessage = getString(R.string.create_streetnumber_error_text)
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

    private fun getEstateCity(): String {
        val createEstateCity = binding.createAddressCityEdit.text.toString()
        if (createEstateCity.isEmpty()) { // https://fr.wikipedia.org/wiki/Y_(Somme)
            // Smallest city name in France is 1 letter length
            errorMessage = getString(R.string.create_city_error_text)
        }
        return createEstateCity
    }

    private fun getEstateAvailability(): Long? {
        return if (binding.editEstateAvailability.isChecked) {
            null
        } else {
            System.currentTimeMillis()
        }
    }

}