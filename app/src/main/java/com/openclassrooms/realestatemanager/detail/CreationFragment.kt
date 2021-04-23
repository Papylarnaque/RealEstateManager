package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
    private val pictureListAdapter = CreatePictureListAdapter(PictureListener { picture ->
        // TODO() Handle picture click for rename / delete picture
    })
    private val args: CreationFragmentArgs by navArgs()
    private var errorMessage: String? = null
    private var imageUrl: String? = null
    private var editMode = false
    private var estateKey = System.currentTimeMillis()

    // Late init var
    private lateinit var binding: FragmentCreationBinding
    private lateinit var viewModel: CreationViewModel
    private lateinit var spinnerTypes: List<String>
    private lateinit var estate: Estate
    private lateinit var spinner: Spinner
    private var listPicture: MutableList<Picture> = ArrayList()
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            viewModel.saveImageFromCamera(bitmap)
        }
    private val selectPicture = registerForActivityResult(GetContentWithMimeTypes()) { uri ->
        uri?.let {
            // TODO() call this as new instance each time gallery is clicked ?
            viewModel.copyImageFromUriToAppFolder(uri)
            listPicture.add(
                Picture(
                    url = uri.toString(),
                    displayName = "New Picture",
                    estateId = estateKey,
                    orderNumber = listPicture.size + 1
                )
            )
            notifyPicturesChanged(listPicture)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (args.estateKey != -1L) {
            estateKey = args.estateKey
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
        binding = FragmentCreationBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(CreationViewModel::class.java)

        binding.createRecyclerviewPictures.adapter = pictureListAdapter
        val mLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.createRecyclerviewPictures.layoutManager = mLayoutManager

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
            estate = it
            if (it != null) {
                binding.estate = it
            }
            setTypeSpinner()
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
                viewModel.saveEstate(editMode, estate, listPicture)
            }
        }

        if (editMode) {
            viewModel.getEstatePictures(estateKey).observe(viewLifecycleOwner, {
                it?.let {
                    listPicture = it.toMutableList()
                    notifyPicturesChanged(it)
                }
            })
            editModeBinding()
        } else {
            setTypeSpinner()
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
                estateType = getEstateType(),
//                estateEmployee = getEstateEmployee(),
                employeeId = 1,
                endTime = getEstateAvailability(),
                estatePois = "Test",
            )
        } else return Estate(
            startTime = estateKey,
            estateCity = getEstateCity(),
            estateCityPostalCode = getEstatePostalCode(),
            estateStreetNumber = getEstateStreetNumber(),
            estateStreet = getEstateStreet(),
            estateRooms = getEstateRooms(),
            estateSurface = getEstateSurface(),
            estatePrice = getEstatePrice(),
            estateDescription = getEstateDescription(),
            estateType = getEstateType(),
//                estateEmployee = getEstateEmployee(),
            employeeId = 1,
            endTime = getEstateAvailability(),
            estatePois = "Test"
        )
    }

    private fun getEstateType(): String {
        return if (spinner.selectedItemId == Spinner.INVALID_ROW_ID) {
            errorMessage = getString(R.string.create_type_error_text)
            ""
        } else {
            spinner.selectedItem.toString()
        }
    }


    //----------- Estate Data for Creation ----------------//

    private fun notifyPicturesChanged(it: List<Picture>) {
        pictureListAdapter.submitList(it as MutableList<Picture>)
    }

    private fun setTypeSpinner() {
        spinner = binding.createEstateTypeSpinner

        viewModel.allTypes().observe(viewLifecycleOwner, { typeList ->
            spinnerTypes = typeList.map { it.typeName }

            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                spinnerTypes
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter


            if (editMode) {
                for (i in 0 until spinner.count) {
                    val s = spinnerTypes[i]
                    if (s == estate.estateType) {
                        spinner.setSelection(i)
                    }
                }
            }

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    // do nothing on selected item
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    errorMessage = getString(R.string.create_type_error_text)
                }
            }
        })

    }

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