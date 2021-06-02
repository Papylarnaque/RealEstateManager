package com.openclassrooms.realestatemanager.detail

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.*
import com.openclassrooms.realestatemanager.databinding.FragmentCreationBinding
import com.openclassrooms.realestatemanager.utils.MimeTypesUtil
import com.openclassrooms.realestatemanager.utils.Utils.getFormattedDateFromMillis
import com.openclassrooms.realestatemanager.utils.infoSnackBar
import com.openclassrooms.realestatemanager.viewmodel.CreationViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

val IMAGE_MIME_TYPE = arrayOf("image/jpeg", "image/png")

class CreationFragment : Fragment() {

    // TODO Keep typed data while rotating device
    private val args: CreationFragmentArgs by navArgs()
    private var errorMessage: String? = null
    private var editMode = false
    private var estateKey = System.currentTimeMillis()
    private var endTime: Long? = null
    private var listPicture: MutableList<Picture> = ArrayList()
    private val pictureListAdapter = CreatePictureListAdapter(
        CreatePictureListener {
            onPictureClicked(it)
        }, DeletePictureListener { onPictureBinClicked(it) })

    // Late init var
    private lateinit var binding: FragmentCreationBinding
    private lateinit var viewModel: CreationViewModel
    private lateinit var detailedEstate: DetailedEstate
    private lateinit var typesSpinner: AutoCompleteTextView
    private lateinit var employeesSpinner: AutoCompleteTextView
    private lateinit var poiChipGroup: ChipGroup
    private lateinit var allTypes: List<Type>
    private lateinit var types: List<String>
    private lateinit var allEmployees: List<Employee>
    private lateinit var employees: List<String>
    private var estatePoisList: List<Poi>? = null

    // Pictures functionality val
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            val pictureUrl = viewModel.saveImageFromCamera(bitmap)
            savePictures(pictureUrl)
        }

    private val selectPicture = registerForActivityResult(MimeTypesUtil()) { uri ->
        uri?.let {
            savePictures(uri.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (args.estateKey != -1L) {
            estateKey = args.estateKey
            editMode = true
            (activity as MainActivity).supportActionBar!!.title =
                getString(R.string.edit_estate_titlebar)
        }

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
        viewModel.getEstate(estateKey).observe(viewLifecycleOwner, {
            this.detailedEstate = it
            if (it != null) {
                binding.detailedEstate = it
                endTime = it.estate?.endTime
            }
            setTypeSpinner()
            setEmployeeSpinner()
            setPoisCheckList()
            bindDates()
            binding.editEstateAvailability.visibility = View.VISIBLE
        })
    }

    /**
     * Global View Bindings
     */
    private fun initBindings() {
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

        binding.createRecyclerviewPictures.apply {
            adapter = pictureListAdapter
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        }
        binding.createTakePicture.setOnClickListener {
            // Open camera for image
            takePicture.launch(null)
            // TODO() NavigateUp from Camera navigates back to the creationFragment
        }

        binding.createPickGallery.setOnClickListener {
            selectPicture.launch(IMAGE_MIME_TYPE)
        }

        binding.editEstateAvailability.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked && endTime == null) {
                binding.editEstateAvailability.text = getString(R.string.edit_estate_sold)
                val builder = MaterialDatePicker.Builder.datePicker()
                builder.setCalendarConstraints(limitRange().build())
                builder.setTitleText(R.string.search_sale_pickerdate_title)
                builder.setTitleText(R.string.edit_sale_pickerdate_title)
                val picker = builder.build()
                picker.show(parentFragmentManager, picker.toString())

                picker.addOnPositiveButtonClickListener {
                    endTime = it
                    bindSoldDate(it)
                }

                picker.addOnNegativeButtonClickListener {
                    binding.editEstateAvailability.isChecked = true
                    binding.editEstateAvailability.text =
                        getString(R.string.edit_estate_availability)
                }
            } else if (isChecked) {
                endTime = null
                binding.createDateSold.visibility = View.INVISIBLE
                binding.editEstateAvailability.text = getString(R.string.edit_estate_availability)
            }
        }

        binding.createSaveEstate.setOnClickListener {
            val estate = shareEstate()
            val pois = getPois()
            if (!errorMessage.isNullOrEmpty()) {
                errorMessage?.let { infoSnackBar(requireView(), it) }
                errorMessage = null
            } else {
                viewModel.saveEstate(estate, listPicture, pois)
            }
        }

    }

    private fun limitRange(): CalendarConstraints.Builder {
        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarEnd: Calendar = Calendar.getInstance()

        val minDate = estateKey // one year
        val maxDate = calendarEnd.timeInMillis // current date

        constraintsBuilderRange.setStart(minDate)
        constraintsBuilderRange.setEnd(maxDate)

        return constraintsBuilderRange
    }

    private fun bindSoldDate(endTime: Long) {
        binding.createDateSold.text = getString(
            R.string.detail_date_sold,
            getFormattedDateFromMillis(endTime)
        )
        binding.createDateSold.visibility = View.VISIBLE
    }


    private fun bindDates() {
        if (detailedEstate.estate?.startTime != null) {
            binding.createDateStart.text = getString(
                R.string.detail_date_start,
                getFormattedDateFromMillis(detailedEstate.estate?.startTime!!)
            )
            binding.createDateStart.visibility = View.VISIBLE
        }

        if (detailedEstate.estate!!.endTime != null) {
            binding.createDateSold.text = getString(
                R.string.detail_date_sold,
                getFormattedDateFromMillis(detailedEstate.estate?.endTime!!)
            )
            binding.createDateSold.visibility = View.VISIBLE
        }
    }

//----------------- MANAGE ESTATE ---------------------//
    /**
     * Handle Estate data
     */
    private fun shareEstate(): Estate {
        return Estate(
            startTime = estateKey,
            endTime = getEstateAvailability(),
            estateCity = getEstateCity(),
            estateCityPostalCode = getEstatePostalCode(),
            estateStreetNumber = getEstateStreetNumber(),
            estateStreet = getEstateStreet(),
            estateRooms = getRooms(),
            estateSurface = getSurface(),
            employeeId = getEmployee(),
            estateDescription = getDescription(),
            estatePrice = getPrice(),
            estateTypeId = getType(),
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
            allTypes = it
            types = it.map { it.typeName }

            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, types)
            typesSpinner.setAdapter(adapter)

            if (editMode)
                detailedEstate.type?.let { t ->
                    typesSpinner.setText(t.typeName, false)
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
            // TODO() in repository
            estatePoisList = detailedEstate.poiList
        }
        poiChipGroup = binding.createPoisChipGroup
        viewModel.allPois().observe(viewLifecycleOwner, {
            poiChipGroup.removeAllViews()
            for (poi in it) {
                val chip = layoutInflater.inflate(
                    R.layout.create_poi,
                    poiChipGroup,
                    false
                ) as Chip
                chip.id = poi.poiId
                chip.text = poi.poiName
                poiChipGroup.addView(chip, poiChipGroup.childCount - 1)
                if (editMode) {
                    if (estatePoisList?.contains(poi) == true)
                        chip.isChecked = true
                }
            }
        })
    }

//----------- Estate Data for Creation ----------------//

    private fun getType(): Int {
        val spinnerIndex = types.indexOf(typesSpinner.text.toString())
        return if (spinnerIndex == -1) {
            errorMessage = getString(R.string.create_type_error_text)
            0
        } else {
            allTypes[spinnerIndex].typeId
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
        val priceMin = resources.getInteger(R.integer.create_price_minimum) - 1
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

    private fun getPois(): List<Int> {
        return poiChipGroup.checkedChipIds.toList()
//        return poiChipGroup.checkedChipIds.joinToString("|", prefix = "|", postfix = "|")
    }

    private fun getEstateAvailability(): Long? {
        return when {
            binding.editEstateAvailability.isChecked -> null
            else -> this.endTime
        }
    }

//------------------- ESTATE SAVED ---------------------//

    private fun navigateAfterSaveClick() {
        // When an item is clicked.
        viewModel.navigateToEstateDetail.observe(viewLifecycleOwner, { estate ->
            estate?.let {
                NavHostFragment.findNavController(this).navigate(
                    CreationFragmentDirections.actionCreationFragmentToDetailFragment()
                )
            } ?: NavHostFragment.findNavController(this)
                .navigate(R.id.action_creationFragment_to_listFragment)
            confirmEstateSaved()
        })
    }

    private fun confirmEstateSaved() = if (editMode)
        infoSnackBar(requireView(), getString(R.string.edit_estate_confirmation))
    else
        infoSnackBar(requireView(), getString(R.string.create_estate_confirmation))


    // ----------- PICTURE CLICK HANDLING ---------//


    private fun onPictureClicked(picture: Picture) {
        openPictureRenameDialog(picture)
    }

    private fun openPictureRenameDialog(picture: Picture) {

        val builder = AlertDialog.Builder(requireContext())

        val editPictureName = EditText(activity)
        editPictureName.setText(picture.displayName)
        with(builder)
        {
            setTitle(getString(R.string.create_picture_click_dialog_title))
            setView(editPictureName)

            val positiveButtonClick = { _: DialogInterface, _: Int ->
                picture.displayName = editPictureName.text.toString()
                notifyPicturesChanged(listPicture)
            }

            val negativeButtonClick = { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

            setPositiveButton(
                getString(R.string.create_picture_click_dialog_positivebutton),
                positiveButtonClick
            )
            setNegativeButton(getString(R.string.cancel_dialog), negativeButtonClick)
            show()
        }
    }

    private fun onPictureBinClicked(picture: Picture) {
        openPictureDeleteDialog(picture)
    }

    private fun openPictureDeleteDialog(picture: Picture) {
        val builder = AlertDialog.Builder(requireContext())

        with(builder)
        {
            setTitle(getString(R.string.create_picture_delete_dialog_title))
            setMessage(getString(R.string.create_picture_delete_dialog_message))

            val positiveButtonClick = { _: DialogInterface, _: Int ->
                viewModel.deletePicture(picture) // async deletion
                listPicture.remove(picture) // delete then refresh view
                notifyPicturesChanged(listPicture)
                infoSnackBar(
                    requireView(),
                    getString(R.string.create_picture_delete_dialog_confirmation)
                )
            }

            val negativeButtonClick = { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

            setPositiveButton(
                getString(R.string.create_picture_delete_dialog_positivebutton),
                positiveButtonClick
            )
            setNegativeButton(getString(R.string.cancel_dialog), negativeButtonClick)
            show()
        }
    }
}