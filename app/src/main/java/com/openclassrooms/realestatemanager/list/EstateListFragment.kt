package com.openclassrooms.realestatemanager.list

import android.os.Bundle
import android.view.*
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

// TODO Keep filter when navigating back from detail fragment ?
// TODO Show that filter is active or not in list
class EstateListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val args: EstateListFragmentArgs by navArgs()
    private val viewModel: ListDetailViewModel by activityViewModels()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getEstates()
        onEstateClick()

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
        viewModel.allDetailedEstates.observeForever {
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
            if (binding.detailFragmentContainer != null && estate == null) {
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
        navController.navigate(
            EstateListFragmentDirections.actionListFragmentToSearchDialogFragment()
        )
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

