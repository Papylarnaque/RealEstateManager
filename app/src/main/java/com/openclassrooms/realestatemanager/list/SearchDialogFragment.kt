package com.openclassrooms.realestatemanager.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.EstateSearch
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding
import com.openclassrooms.realestatemanager.utils.Utils.getFormattedDateFromMillis
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel
import java.util.*

class SearchDialogFragment : DialogFragment(R.layout.fragment_search) {

    private val viewModel: ListDetailViewModel by activityViewModels()
    private lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSearchBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        super.onViewCreated(view, savedInstanceState)
        setDialogSize()
        initBindings()
        configDialogActions()
    }

    private fun setDialogSize() {
        if (requireContext().resources.getBoolean(R.bool.isTablet)) {
            dialog?.window?.setLayout(
                1200,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } else {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun initBindings() {
        setTypeSpinner()
        setPriceSlider()
        setSurfaceSlider()
        setCreationDatePicker()
        soldStatus = false
        setSoldDateSwitchAndPicker()
        setPictureNumberToggle()
        setPoisCheckList()
    }

    private fun setTypeSpinner() {
        viewModel.allTypes().observe(viewLifecycleOwner, { it ->
            val types = it.map { it.typeName }
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, types)
            binding.searchEstateTypeSpinnerView.setAdapter(adapter)
        })

        //TODO not observed ?
        binding.searchEstateTypeSpinnerView.onItemSelectedListener.let {
            viewModel.updateType(binding.searchEstateTypeSpinnerView.text.toString())
        }

    }

    private fun setPriceSlider() {
        val minPrice = 0f
        val maxPrice = 100000000f
        val stepPrice = 1000000f
        with(binding.searchPrice) {
            this.valueFrom = minPrice
            this.valueTo = maxPrice
            this.values = mutableListOf(minPrice, maxPrice)
            this.stepSize = stepPrice
        }
    }

    private fun setSurfaceSlider() {
        val minSurface = 0f
        val maxSurface = 10000f
        val stepSurface = 100f
        with(binding.searchSurface) {
            this.valueFrom = minSurface
            this.valueTo = maxSurface
            this.values = mutableListOf(minSurface, maxSurface)
            this.stepSize = stepSurface
        }
    }

    private fun setCreationDatePicker() {
        binding.searchCreateDate.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setCalendarConstraints(limitRange().build())
            builder.setTitleText(R.string.search_creation_pickerdate_title)
            val picker = builder.build()

            picker.show(parentFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {
                val startDate = getFormattedDateFromMillis(it.first)
                val endDate = getFormattedDateFromMillis(it.second)

                if (it.first != null) searchCreationStartDate = it.first!!
                if (it.second != null) searchCreationEndDate = it.second!!

                if (it.first == it.second) {
                    binding.searchCreateDate.text =
                        getString(R.string.search_create_date_oneday, startDate.toString())
                } else {
                    binding.searchCreateDate.text =
                        getString(
                            R.string.search_create_date,
                            startDate.toString(),
                            endDate.toString()
                        )
                }
            }

        }
    }


    private fun setSoldDateSwitchAndPicker() {
        binding.searchSoldSwitch.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) {
                binding.searchSoldDate.visibility =
                    View.GONE
                binding.searchSoldSwitch.text = getString(R.string.search_endtime_switch_available)
                soldStatus = false
            } else {
                setSoldDatePicker()
                binding.searchSoldSwitch.text = getString(R.string.search_endtime_switch_sold)
                soldStatus = true
            }
        }
    }

    private fun setSoldDatePicker() {
        with(binding.searchSoldDate) {
            this.visibility = View.VISIBLE
            this.setOnClickListener {
                val builder = MaterialDatePicker.Builder.dateRangePicker()
                builder.setCalendarConstraints(limitRange().build())
                builder.setTitleText(R.string.search_sale_pickerdate_title)
                val picker = builder.build()

                picker.show(parentFragmentManager, picker.toString())

                picker.addOnPositiveButtonClickListener {
                    val startDate = getFormattedDateFromMillis(it.first)
                    val endDate = getFormattedDateFromMillis(it.second)

                    if (it.first != null) searchSoldStartDate = it.first!!
                    if (it.second != null) searchSoldEndDate = it.second!!

                    if (it.first == it.second) {
                        this.text =
                            getString(R.string.search_sold_date_oneday, startDate.toString())
                    } else {
                        this.text =
                            getString(
                                R.string.search_sold_date,
                                startDate.toString(),
                                endDate.toString()
                            )
                    }
                }
            }
        }
    }

    private fun limitRange(): CalendarConstraints.Builder {
        val constraintsBuilderRange = CalendarConstraints.Builder()

        val calendarEnd: Calendar = Calendar.getInstance()

        val minDate = 0L // one year
        val maxDate = calendarEnd.timeInMillis // current date

        constraintsBuilderRange.setStart(minDate)
        constraintsBuilderRange.setEnd(maxDate)

        return constraintsBuilderRange
    }


    private fun setPictureNumberToggle() {
        with(binding.searchPictureTogglegroup) {
            for (pictureNumber in 1.rangeTo(4)) {
                val pictureButton = layoutInflater.inflate(
                    R.layout.search_picture_materialbutton,
                    this,
                    false
                ) as MaterialButton
                pictureButton.text = pictureNumber.toString()

                binding.searchPictureTogglegroup.addView(
                    pictureButton,
                    this.childCount
                )

            }
            this.addOnButtonCheckedListener { group, _, _ ->
                searchPictureNumber =
                    this.findViewById<MaterialButton>(group.checkedButtonId).text.toString()
                        .toInt()
            }
        }
    }

    private fun setPoisCheckList() {
        with(binding.searchPoi) {
            viewModel.allPois().observe(viewLifecycleOwner, {
                this.removeAllViews()
                for (poi in it) {
                    val chip = layoutInflater.inflate(
                        R.layout.create_poi,
                        this,
                        false
                    ) as Chip
                    chip.id = poi.poiId
                    chip.text = poi.poiName
                    this.addView(chip, this.childCount - 1)
                }
            })
        }
    }

    private fun configDialogActions() {
        binding.searchTitle.text = getString(R.string.search_estate)
        binding.searchCancel.setOnClickListener {
            dismiss()
        }
        binding.searchButton.setOnClickListener {
            filterEstateList()
            navigateToList()
        }
        binding.searchReset.setOnClickListener {
            viewModel.filterEstate(null)
            navigateToList()
        }
    }

    private fun navigateToList() {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        ).navigate(R.id.listFragment)
    }

    private fun filterEstateList() {
        viewModel.filterEstate(
            EstateSearch(
                type = binding.searchEstateTypeSpinnerView.text.toString(),
                priceRange = IntRange(
                    start = binding.searchPrice.values.first().toInt(),
                    endInclusive = binding.searchPrice.values.last().toInt()
                ),
                surfaceRange = IntRange(
                    start = binding.searchSurface.values.first().toInt(),
                    endInclusive = binding.searchSurface.values.last().toInt()
                ),
                createDateRange = LongRange(
                    start = searchCreationStartDate,
                    endInclusive = searchCreationEndDate
                ),
                soldStatus = soldStatus,
                soldDateRange = LongRange(
                    start = searchSoldStartDate,
                    endInclusive = searchSoldEndDate
                ),
                pictureMinNumber = searchPictureNumber,
                poiList = binding.searchPoi.checkedChipIds
            )
        )
    }

    companion object {
        var searchCreationStartDate: Long = 0L // oldest calendar possible
        var searchCreationEndDate: Long = Calendar.getInstance().timeInMillis
        var soldStatus: Boolean = false
        var searchSoldStartDate: Long = 0L // oldest calendar possible
        var searchSoldEndDate: Long = Calendar.getInstance().timeInMillis
        var searchPictureNumber: Int = 0
        var searchPois: MutableList<Int>? = null
    }

}