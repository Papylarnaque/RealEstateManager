package com.openclassrooms.realestatemanager.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.infoSnackBar
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel

class EstateListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val args: EstateListFragmentArgs by navArgs()
    private val viewModel: ListDetailViewModel by activityViewModels()
    private var estateId: Long? = null
    private val estateListAdapter = EstateListAdapter(EstateListener {
        viewModel.onEstateClicked(it)
        view?.isSelected = true
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        initBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initEstates()
        estatesObserver()
        onEstateClickObserver()
        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_list_detail, menu)
        if (estateId != null) {
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
            R.id.search_estate -> navigateSearchDialog()
            R.id.settings_daynight -> navigateDayNightSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initBindings() {
        binding = FragmentListBinding.inflate(layoutInflater)
        binding.recyclerviewEstateList.apply {
            adapter = estateListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun estatesObserver() {
        viewModel.allDetailedEstates.observe(viewLifecycleOwner) {
            notifyListChanged(it)
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
            // Select first estate by default on Tablet mode
            if (binding.detailFragmentContainer != null && estateId == null) {
                binding.detailFragmentContainer!!.visibility = View.VISIBLE
                viewModel.onEstateClicked(list[0])
            }
        }
    }

    private fun onEstateClickObserver() {
        viewModel.navigateToEstateDetail.observe(viewLifecycleOwner) { it ->
            it.let {
                estateId = it
                if (it != null) {
                    // If SINGLE layout mode
                    if (!requireContext().resources.getBoolean(R.bool.isTablet)) {
                        navController.navigate(
                            EstateListFragmentDirections
                                .actionListFragmentToDetailFragment()
                        )
                    }
                    // DUAL layout
                    else {
                        childFragmentManager.beginTransaction()
                            .replace(binding.detailFragmentContainer!!.id, DetailFragment())
                            .commit()

                        binding.recyclerviewEstateList.background =
                            getDrawable(requireContext(), R.drawable.border_right)
                    }
                }
            }
        }
        viewModel.stopEstateNavigation()
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
                    estateId!!
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

    private fun navigateSearchDialog() {
        navController.navigate(
            EstateListFragmentDirections.actionListFragmentToSearchDialogFragment()
        )
    }

    private fun navigateDayNightSettings() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.dayNightFragment)
    }

}

