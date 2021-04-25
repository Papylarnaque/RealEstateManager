package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Employee
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.databinding.FragmentCreationBinding
import com.openclassrooms.realestatemanager.utils.GetContentWithMimeTypes
import com.openclassrooms.realestatemanager.utils.KUtil
import com.openclassrooms.realestatemanager.viewmodel.CreationViewModel
import kotlin.math.abs

val IMAGE_MIME_TYPE = arrayOf("image/jpeg", "image/png")

class CreationFragment : Fragment() {

    // TODO Keep typed data while rotating device
    private val pictureListAdapter = CreatePictureListAdapter(CreatePictureListener {
        //   picture ->
        //   TODO() Handle picture click for rename / delete picture
    })
    private val args: CreationFragmentArgs by navArgs()
    private var errorMessage: String? = null
    private var editMode = false
    private var estateKey = System.currentTimeMillis()
    private lateinit var allEmployees: List<Employee>

    // Late init var
    private lateinit var binding: FragmentCreationBinding
    private lateinit var viewModel: CreationViewModel
    private lateinit var detailedEstate: DetailedEstate
    private var listPicture: MutableList<Picture> = ArrayList()
    private lateinit var typesSpinner: AutoCompleteTextView
    private lateinit var employeesSpinner: AutoCompleteTextView
    private lateinit var poiChipGroup: ChipGroup
    private lateinit var types: List<String>
    private lateinit var employees: List<String>
    private lateinit var pois: List<String>

    // Pictures functionality val
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            val pictureUrl = viewModel.saveImageFromCamera(bitmap)
            savePictures(pictureUrl)
        }

    private val selectPicture = registerForActivityResult(GetContentWithMimeTypes()) { uri ->
        uri?.let {
            savePictures(uri.toString())
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
            this.detailedEstate = it
            if (it != null) {
                binding.detailedEstate = it
            }
            setTypeSpinner()
            setEmployeeSpinner()
            setPoisCheckList()
            binding.editEstateAvailability.visibility = View.VISIBLE
        })
    }

    /**
     * Global View Bindings
     */
    private fun initBindings() {
        binding.createRecyclerviewPictures.adapter = pictureListAdapter
        val mLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.createRecyclerviewPictures.layoutManager = mLayoutManager

        binding.createTakePicture.setOnClickListener {
            // Open camera for image
            takePicture.launch(null)
            // TODO() NavigateUp from Camera navigates back to the creationFragment
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
            setEmployeeSpinner()
            setPoisCheckList()
        }


    }

    //----------------- MANAGE ESTATE ---------------------//
    /**
     * Handle Estate data
     */
    private fun shareEstate(): Estate {
        return Estate(
            startTime = estateKey,
            estateCity = getEstateCity(),
            estateCityPostalCode = getEstatePostalCode(),
            estateStreetNumber = getEstateStreetNumber(),
            estateStreet = getEstateStreet(),
            estateRooms = getRooms(),
            estateSurface = getSurface(),
            estatePrice = getPrice(),
            estateDescription = getDescription(),
            estateTypeId = getType(),
            employeeId = getEmployee(),
            endTime = getEstateAvailability(),
            estatePois = getPois()
        )
    }

    //----------- Estate Data for Creation ----------------//

    private fun savePictures(pictureUrl: String) {
        listPicture.add(
            Picture(
                url = pictureUrl,
                estateId = estateKey,
                orderNumber = listPicture.size + 1
            )
        )
        notifyPicturesChanged(listPicture)
    }

    private fun notifyPicturesChanged(it: List<Picture>) {
        pictureListAdapter.submitList(it as MutableList<Picture>)
        pictureListAdapter.notifyDataSetChanged()
    }


    //----------- Spinners Configuration ----------------//

    private fun setTypeSpinner() {
        typesSpinner = binding.createEstateTypeSpinnerEdit

        viewModel.allTypes().observe(viewLifecycleOwner, { it ->
            types = it.map { it.typeName }

            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, types)
            typesSpinner.setAdapter(adapter)

            if (editMode)
                detailedEstate.type?.let { t ->
                    typesSpinner.setSelection(t.typeId)
                }
            else
                typesSpinner.setSelection(0)
        })

    }

    private fun setEmployeeSpinner() {
        employeesSpinner = binding.createEstateEmployeeSpinnerEdit

        viewModel.allEmployees().observe(viewLifecycleOwner, { it ->
            allEmployees = it
            employees = it.map { it.employeeFullName }

            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, employees)
            employeesSpinner.setAdapter(adapter)

            if (editMode)
                detailedEstate.employee.let { e ->
                    employeesSpinner.setText(e!!.employeeFullName, false)
                }
            else
                employeesSpinner.setSelection(0)

            employeesSpinner
        })
    }

    private fun setPoisCheckList() {
        if (editMode) {
            pois = detailedEstate.estate?.estatePois!!.split("|")
        }

        poiChipGroup = binding.createPoisChipGroup
        viewModel.allPois().observe(viewLifecycleOwner, { it ->
            val pois = it.map { it.poiName }
            for (string in pois) {
                val chip = layoutInflater.inflate(
                    R.layout.create_poi,
                    poiChipGroup,
                    false
                ) as Chip
                chip.id = pois.indexOf(string)
                chip.text = string;
                poiChipGroup.addView(chip, poiChipGroup.childCount - 1);
                if (editMode) {
                    if (this.pois.contains(pois.indexOf(string).toString()))
                        chip.isChecked = true
                }
            }
        })
    }

    //----------- Estate Data for Creation ----------------//


    private fun getType(): Int {
        return if (typesSpinner.selectionStart == 0) {
            errorMessage = getString(R.string.create_type_error_text)
            0
        } else {
            types.indexOf(typesSpinner.text.toString()) + 1
        }
    }

    private fun getEmployee(): Int {
        val spinnerIndex = employees.indexOf(employeesSpinner.text.toString())
        return if (spinnerIndex == -1) {
            errorMessage = getString(R.string.create_employee_error_text)
            0
        } else {
            allEmployees[spinnerIndex].employeeId
        }
    }

    private fun getDescription(): String {
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

    private fun getPrice(): Int {
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

    private fun getSurface(): Int? {
        return if (binding.createSurfaceEdit.text.toString().isBlank()) {
            null
        } else {
            return binding.createSurfaceEdit.text.toString().toInt()
        }
    }

    private fun getRooms(): Int? {
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

    private fun getPois(): String {
        return poiChipGroup.checkedChipIds.joinToString("|")
    }

    private fun getEstateAvailability(): Long? {
        return if (binding.editEstateAvailability.isChecked) {
            null
        } else {
            System.currentTimeMillis()
        }
    }

    //------------------- ESTATE SAVED ---------------------//

    private fun navigateAfterSaveClick() {
        // When an item is clicked.
        viewModel.navigateToEstateDetail.observe(viewLifecycleOwner, { estate ->
            estate?.let {
                NavHostFragment.findNavController(this).navigateUp()
            } ?: NavHostFragment.findNavController(this)
                .navigate(R.id.action_creationFragment_to_listFragment)

            confirmEstateSaved()
        })
    }

    private fun confirmEstateSaved() = if (editMode)
        KUtil.infoSnackBar(requireView(), getString(R.string.edit_estate_confirmation))
    else
        KUtil.infoSnackBar(requireView(), getString(R.string.create_estate_confirmation))


}