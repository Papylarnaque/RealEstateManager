package com.openclassrooms.realestatemanager.detail

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.*
import com.openclassrooms.realestatemanager.databinding.FragmentCreationBinding
import com.openclassrooms.realestatemanager.utils.GetContentWithMimeTypes
import com.openclassrooms.realestatemanager.utils.KUtil
import com.openclassrooms.realestatemanager.viewmodel.CreationViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

val IMAGE_MIME_TYPE = arrayOf("image/jpeg", "image/png")
const val DATEFORMAT = "dd/MM/yyyy"

class CreationFragment : Fragment() {

    // TODO Keep typed data while rotating device
    private val args: CreationFragmentArgs by navArgs()
    private var errorMessage: String? = null
    private var editMode = false
    private var estateKey = System.currentTimeMillis()
    private var endTime: Long? = null
    private val formatter = SimpleDateFormat(DATEFORMAT, Locale.US)
    private val calendar: Calendar = Calendar.getInstance()
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
    private lateinit var estatePoisIdList: List<String>

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
        viewModel.getEstateWithId(args.estateKey).observe(viewLifecycleOwner, {
            this.detailedEstate = it
            if (it != null) {
                binding.detailedEstate = it
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

        binding.editEstateAvailability.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked && detailedEstate.estate?.endTime == null) {
                // Get Current Date
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]


                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        val c1 = Calendar.getInstance()
                        c1.set(Calendar.YEAR, year)
                        c1.set(Calendar.MONTH, monthOfYear)
                        c1.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        endTime = c1.timeInMillis
                        bindSoldDate()
                    }, mYear, mMonth, mDay
                )

                datePickerDialog.setTitle(R.string.create_pickerdate_title)
                datePickerDialog.datePicker.minDate = estateKey
                datePickerDialog.show()

            } else if (isChecked) {
                binding.createDateSold.visibility = View.INVISIBLE
            }
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

    private fun bindSoldDate() {
        if (endTime!! < estateKey) {
            KUtil.infoSnackBar(requireView(), getString(R.string.create_endtime_error_text))
        } else {
            calendar.timeInMillis = endTime!!
            binding.createDateSold.text = getString(
                R.string.detail_date_sold,
                formatter.format(calendar.time)
            )
            binding.createDateSold.visibility = View.VISIBLE
        }
    }


    private fun bindDates() {
        calendar.timeInMillis = detailedEstate.estate?.startTime!!
        binding.createDateStart.text = getString(
            R.string.detail_date_start,
            formatter.format(calendar.time)
        )

        if (detailedEstate.estate!!.endTime != null) {
            calendar.timeInMillis = detailedEstate.estate!!.endTime!!
            binding.createDateSold.text = getString(
                R.string.detail_date_sold,
                formatter.format(calendar.time)
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
            estatePois = getPois(),
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
            estatePoisIdList = detailedEstate.estate?.estatePois!!.split("|")
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
                poiChipGroup.addView(chip, poiChipGroup.childCount - 1);
                if (editMode) {
                    if (estatePoisIdList.contains(chip.id.toString()))
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


    // ----------- PICTURE CLICK HANDLING ---------//


    private fun onPictureClicked(picture: Picture) {
        //   TODO() Handle picture click for rename / delete picture
        openPictureRenameDialog(picture)
    }

    private fun openPictureRenameDialog(picture: Picture) {

        val builder = AlertDialog.Builder(requireContext())

        val editPictureName = EditText(activity);
        editPictureName.setText(picture.displayName)
        with(builder)
        {
            setTitle(getString(R.string.create_picture_click_dialog_title))
            setView(editPictureName)

            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                picture.displayName = editPictureName.text.toString()
                viewModel.insertPicture(picture)
                notifyPicturesChanged(listPicture)
            }

            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }

            setPositiveButton(getString(R.string.create_picture_click_dialog_positivebutton), positiveButtonClick)
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

            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                viewModel.deletePicture(picture) // async deletion
                listPicture.remove(picture) // delete then refresh view
                notifyPicturesChanged(listPicture)
                KUtil.infoSnackBar(requireView(), getString(R.string.create_picture_delete_dialog_confirmation))
            }

            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }

            setPositiveButton(getString(R.string.create_picture_delete_dialog_positivebutton), positiveButtonClick)
            setNegativeButton(getString(R.string.cancel_dialog), negativeButtonClick)
            show()
        }
    }

}