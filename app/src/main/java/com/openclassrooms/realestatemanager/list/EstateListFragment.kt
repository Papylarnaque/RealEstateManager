package com.openclassrooms.realestatemanager.list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.utils.Utils.isInternetAvailable
import com.openclassrooms.realestatemanager.utils.infoSnackBar
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel

// TODO() Implement Filter depending on PRICE & AVAILABILITY
class EstateListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val viewModel: ListDetailViewModel by viewModels()
    private var detailedEstatesList: List<DetailedEstate> = emptyList()
    private var estate: DetailedEstate? = null
    private val estateListAdapter = EstateListAdapter(EstateListener {
        viewModel.onEstateClicked(it)
    })

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
        //get item id to handle item clicks
        when (item.itemId) {

            R.id.add_estate -> {
                Log.i("EstateListFragment", "Click on create a new estate")
                NavHostFragment.findNavController(this)
                    .navigate(EstateListFragmentDirections.actionListFragmentToCreationFragment(-1L))
            }

            R.id.open_map -> {
                if (isInternetAvailable(context)) {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.mapFragment)
                } else {
                    infoSnackBar(binding.root, getString(R.string.internet_required))
                }
            }

            R.id.edit_estate -> {
                Log.i("EstateListFragment", "Click on edit an estate")
                NavHostFragment.findNavController(this)
                    .navigate(EstateListFragmentDirections.actionListFragmentToCreationFragment(
                        estate?.estate?.startTime!!
                    ))
            }

            R.id.search_estate -> {
                Log.i("EstateListFragment", "Click on search an estate")
                searchEstateDialog()
            }
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
                notifyListChanged()
            }
        }
    }

    private fun notifyListChanged() {
        estateListAdapter.submitList(detailedEstatesList as MutableList<DetailedEstate>)
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

        val customDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()

        with(customDialog)
        {
            setTitle(getString(R.string.search_type_title_text))
            findViewById<Button>(R.id.search_cancel).setOnClickListener {
                customDialog.dismiss()
            }
            show()
        }
    }


    private fun setTypeSpinner(dialogView: View) {
        val typesSpinner : AutoCompleteTextView = dialogView.findViewById(R.id.searchEstateTypeSpinnerView)

        viewModel.allTypes().observe(viewLifecycleOwner, { it ->
            val types = it.map { it.typeName }
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, types)
            typesSpinner.setAdapter(adapter)

        })

    }

}

