package com.openclassrooms.realestatemanager.list

import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.openclassrooms.realestatemanager.utils.KUtil
import com.openclassrooms.realestatemanager.utils.Utils.isInternetAvailable
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel

// TODO() Implement Filter depending on PRICE & AVAILABILITY
class EstateListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val viewModel: ListDetailViewModel by viewModels()
    private var detailedEstatesList: List<DetailedEstate> = emptyList()
    private lateinit var estate: DetailedEstate
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
        if (binding.detailFragmentContainer != null) {
            inflater.inflate(R.menu.menu_fragment_list_detail, menu)
        } else {
            inflater.inflate(R.menu.menu_fragment_list, menu)
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
                //Open CreationFragment
                Log.i("EstateListFragment", "Click on create a new estate")
                NavHostFragment.findNavController(this)
                    .navigate(EstateListFragmentDirections.actionListFragmentToCreationFragment(-1L))
            }

            R.id.open_map -> {
                if (isInternetAvailable(context)) {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.mapFragment)
                } else {
                    KUtil.infoSnackBar(binding.root, getString(R.string.internet_required))
                }
            }

            R.id.edit_estate -> {
                //Open CreationFragment for Edition
                Log.i("EstateListFragment", "Click on edit an estate")
                NavHostFragment.findNavController(this)
                    .navigate(EstateListFragmentDirections.actionListFragmentToCreationFragment(
                        estate.estate?.startTime!!
                    ))
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
            it.let {
                estate = it
                // If SINGLE layout mode
                // if (binding.detailFragmentContainer == null) {
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


}

