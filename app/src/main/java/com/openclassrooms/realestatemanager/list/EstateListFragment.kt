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
import com.google.android.material.slider.RangeSlider
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.EstateSearch
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.infoSnackBar
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel

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
        estateListAdapter.submitList(list as MutableList<DetailedEstate>)
        if (args.estateKey != -1L && requireContext().resources.getBoolean(R.bool.isTablet)) {
            for (estate in list) {
                if (estate.estate?.startTime == args.estateKey) viewModel.onEstateClicked(estate)
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


    /**
     * Handle estate search
     */
    private fun searchEstateDialog() {
        val dialogView = layoutInflater.inflate(R.layout.search_dialog, null)
        setTypeSpinner(dialogView)
        setPriceSlider(dialogView)
        setSurfaceSlider(dialogView)

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
            )
        )
        viewModel.filterEstateList(searchEstate).observe(viewLifecycleOwner, {
            notifyListChanged(it)
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


}

