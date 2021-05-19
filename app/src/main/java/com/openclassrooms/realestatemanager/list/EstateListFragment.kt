package com.openclassrooms.realestatemanager.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.EstateSearch
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.Utils.getFormattedDateFromMillis
import com.openclassrooms.realestatemanager.utils.infoSnackBar
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel
import java.util.*

// TODO Keep filter when navigating back from detail fragment ?
// TODO Show that filter is active or not in list
class EstateListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val args: EstateListFragmentArgs by navArgs()
    private val viewModel: ListDetailViewModel by viewModels()
    private var detailedEstatesList: List<DetailedEstate> = emptyList()
    private var estate: DetailedEstate? = null
    private val estateListAdapter = EstateListAdapter(EstateListener {
        viewModel.onEstateClicked(it)
    })
    private lateinit var typesSpinner: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        initBindings()
        getEstates()
        onEstateClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_list_detail, menu)
        if (estate != null) {
            menu.findItem(R.id.edit_estate).isVisible = true
            menu.findItem(R.id.convert_price).isVisible = true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handle item clicks of menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_estate -> navigateCreateEstate()
            R.id.open_map -> navigateMapView()
            R.id.edit_estate -> navigateEditEstate()
            R.id.search_estate -> searchEstateDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initBindings() {
        binding = FragmentListBinding.inflate(layoutInflater)
        binding.recyclerviewEstateList.adapter = estateListAdapter
        binding.recyclerviewEstateList.layoutManager = LinearLayoutManager(context)
    }

    private fun getEstates() {
        viewModel.allDetailedEstates.observe(viewLifecycleOwner) {
            it.let {
                detailedEstatesList = it
                notifyListChanged(it)
            }
        }
    }

    private fun notifyListChanged(list: List<DetailedEstate>) {
        if (list.isEmpty()) {
            binding.recyclerviewEstateList.visibility = View.INVISIBLE
            binding.emptyList.visibility = View.VISIBLE
            if (binding.detailFragmentContainer != null) {
                binding.detailFragmentContainer!!.visibility = View.INVISIBLE
            }
        } else {
            binding.emptyList.visibility = View.INVISIBLE
            binding.recyclerviewEstateList.visibility = View.VISIBLE
            estateListAdapter.submitList(list as MutableList<DetailedEstate>)
            if (args.estateKey != -1L && requireContext().resources.getBoolean(R.bool.isTablet)) {
                for (estate in list) {
                    if (estate.estate?.startTime == args.estateKey) viewModel.onEstateClicked(estate)
                }
            }
            if (binding.detailFragmentContainer != null) {
                binding.detailFragmentContainer!!.visibility = View.VISIBLE
                viewModel.onEstateClicked(list[0])
            }

        }
    }

    private fun onEstateClick() {
        // When an item is clicked.
        viewModel.navigateToEstateDetail.observe(viewLifecycleOwner) { it ->
            it?.let {
                estate = it
                // If SINGLE layout mode
                if (!requireContext().resources.getBoolean(R.bool.isTablet)) {
                    navController.navigate(
                        EstateListFragmentDirections
                            .actionListFragmentToDetailFragment(it.estate!!.startTime)
                    )
                }
                // If LANDSCAPE and MASTER-DETAIL dual layout
                else {
                    childFragmentManager.beginTransaction()
                        .replace(binding.detailFragmentContainer!!.id, DetailFragment())
                        .commit()
                }
            }
        }
    }

// TODO() How to export that search dialog to another file or function ?

    /**
     * Handle estate search
     */
    private fun searchEstateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.search_dialog, null)
        with(dialogView) {
            setTypeSpinner(this)
            setPriceSlider(this)
            setSurfaceSlider(this)
            setCreationDatePicker(this)
            soldStatus = false
            setSoldDateSwitchAndPicker(this)
        }

        val customDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()

        with(customDialog)
        {
            setTitle(getString(R.string.search_type_title_text))
            findViewById<Button>(R.id.search_cancel).setOnClickListener {
                customDialog.dismiss()
            }
            findViewById<Button>(R.id.search_button).setOnClickListener {
                filterEstateList(dialogView)
                customDialog.dismiss()
            }
            findViewById<Button>(R.id.search_reset).setOnClickListener {
                notifyListChanged(detailedEstatesList)
                customDialog.dismiss()
            }
            show()
        }
    }


    private fun setTypeSpinner(dialogView: View) {
        typesSpinner = dialogView.findViewById(R.id.searchEstateTypeSpinnerView)
        viewModel.allTypes().observe(viewLifecycleOwner, { it ->
            val types = it.map { it.typeName }
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, types)
            typesSpinner.setAdapter(adapter)
        })
    }

    private fun setPriceSlider(dialogView: View) {
        val minPrice = 0f
        val maxPrice = 100000000f
        val stepPrice = 1000000f
        val priceRangeSlider = dialogView.findViewById<RangeSlider>(R.id.search_price)
        priceRangeSlider.valueFrom = minPrice
        priceRangeSlider.valueTo = maxPrice
        priceRangeSlider.values = mutableListOf(minPrice, maxPrice)
        priceRangeSlider.stepSize = stepPrice
    }

    private fun setSurfaceSlider(dialogView: View) {
        val minSurface = 0f
        val maxSurface = 10000f
        val stepSurface = 100f
        val surfaceRangeSlider = dialogView.findViewById<RangeSlider>(R.id.search_surface)
        surfaceRangeSlider.valueFrom = minSurface
        surfaceRangeSlider.valueTo = maxSurface
        surfaceRangeSlider.values = mutableListOf(minSurface, maxSurface)
        surfaceRangeSlider.stepSize = stepSurface
    }


    private fun setCreationDatePicker(dialogView: View) {
        val creationDatePicker = dialogView.findViewById<MaterialTextView>(R.id.search_create_date)

        creationDatePicker.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setCalendarConstraints(limitRange().build())
            val picker = builder.build()

            builder.setTitleText(R.string.create_pickerdate_title)
            picker.show(parentFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {

            }
            picker.addOnNegativeButtonClickListener {

            }
            picker.addOnPositiveButtonClickListener {
                val startDate = getFormattedDateFromMillis(it.first)
                val endDate = getFormattedDateFromMillis(it.second)

                if (it.first != null) searchCreationStartDate = it.first!!
                if (it.second != null) searchCreationEndDate = it.second!!

                if (it.first == it.second) {
                    creationDatePicker.text =
                        getString(R.string.search_create_date_oneday, startDate.toString())
                } else {
                    creationDatePicker.text =
                        getString(
                            R.string.search_create_date,
                            startDate.toString(),
                            endDate.toString()
                        )
                }
            }

        }
    }


    private fun setSoldDateSwitchAndPicker(dialogView: View) {
        val soldDateSwitch = dialogView.findViewById<SwitchMaterial>(R.id.search_sold_switch)
        soldDateSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked) {
                setSoldDatePicker(dialogView)
                soldStatus = true
            }
            else {
                dialogView.findViewById<MaterialTextView>(R.id.search_sold_date).visibility =
                    View.GONE
                soldStatus = false
            }
        }
    }

    private fun setSoldDatePicker(dialogView: View) {
        val soldDatePicker = dialogView.findViewById<MaterialTextView>(R.id.search_sold_date)
        soldDatePicker.visibility = View.VISIBLE

        soldDatePicker.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setCalendarConstraints(limitRange().build())
            val picker = builder.build()

            builder.setTitleText(R.string.create_pickerdate_title)
            picker.show(parentFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {
                val startDate = getFormattedDateFromMillis(it.first)
                val endDate = getFormattedDateFromMillis(it.second)

                if (it.first != null) searchSoldStartDate = it.first!!
                if (it.second != null) searchSoldEndDate = it.second!!

                if (it.first == it.second) {
                    soldDatePicker.text =
                        getString(R.string.search_sold_date_oneday, startDate.toString())
                } else {
                    soldDatePicker.text =
                        getString(
                            R.string.search_sold_date,
                            startDate.toString(),
                            endDate.toString()
                        )
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


    private fun filterEstateList(dialogView: View) {
        val searchEstate = EstateSearch(
            type = dialogView.findViewById<AutoCompleteTextView>(R.id.searchEstateTypeSpinnerView).text.toString(),
            priceRange = IntRange(
                start = dialogView.findViewById<RangeSlider>(R.id.search_price).values.first()
                    .toInt(),
                endInclusive = dialogView.findViewById<RangeSlider>(R.id.search_price).values.last()
                    .toInt()
            ),
            surfaceRange = IntRange(
                start = dialogView.findViewById<RangeSlider>(R.id.search_surface).values.first()
                    .toInt(),
                endInclusive = dialogView.findViewById<RangeSlider>(R.id.search_surface).values.last()
                    .toInt()
            ),
            createDateRange = LongRange(
                start = searchCreationStartDate, endInclusive = searchCreationEndDate
            ),
            soldStatus = soldStatus,
            soldDateRange = LongRange(
                start = searchSoldStartDate, endInclusive = searchSoldEndDate
            )
        )
        viewModel.filterEstateList(searchEstate).observe(viewLifecycleOwner, {
            notifyListChanged(it)
            infoSnackBar(binding.root, getString(R.string.search_notification))
        })
    }


    // NAVIGATION

    private fun navigateCreateEstate() {
        NavHostFragment.findNavController(this)
            .navigate(EstateListFragmentDirections.actionListFragmentToCreationFragment(-1L))
    }

    private fun navigateEditEstate() {
        NavHostFragment.findNavController(this)
            .navigate(
                EstateListFragmentDirections.actionListFragmentToCreationFragment(
                    estate?.estate?.startTime!!
                )
            )
    }

    private fun navigateMapView() {
        if (Utils.isInternetAvailable(context)) {
            NavHostFragment.findNavController(this)
                .navigate(R.id.mapFragment)
        } else {
            infoSnackBar(binding.root, getString(R.string.internet_required))
        }
    }

    companion object {
        var searchCreationStartDate: Long = 0L // oldest calendar possible
        var searchCreationEndDate: Long = Calendar.getInstance().timeInMillis
        var soldStatus: Boolean = false
        var searchSoldStartDate: Long = 0L // oldest calendar possible
        var searchSoldEndDate: Long = Calendar.getInstance().timeInMillis
    }

}

