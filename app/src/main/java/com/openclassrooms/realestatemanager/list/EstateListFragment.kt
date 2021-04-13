package com.openclassrooms.realestatemanager.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.viewmodel.EstateListViewModel


class EstateListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true);

        // Get the viewModel
        val viewModel = ViewModelProvider(this).get(EstateListViewModel::class.java)

        // Inflate view and obtain an instance of the binding class
        binding = FragmentListBinding.inflate(layoutInflater)
        // Handle estate item
        val estateListAdapter = EstateListAdapter(EstateListener { estate ->
            viewModel.onEstateClicked(estate)
        })
        binding.recyclerviewEstateList.adapter = estateListAdapter
        binding.recyclerviewEstateList.layoutManager = LinearLayoutManager(context)

        // Observe data modification in the VM
        viewModel.allEstates.observe(viewLifecycleOwner, {
            it?.let {
                estateListAdapter.submitList(it as MutableList<Estate>)
            }
        })

        // When an item is clicked.
        viewModel.navigateToEstateDetail.observe(viewLifecycleOwner, { estate ->
            estate?.let {
                // If SINGLE layout mode
                if (binding.detailFragmentContainer == null) {
                    navController.navigate(
                            EstateListFragmentDirections
                                    .actionListFragmentToDetailFragment(estate.startTime)
                    )
                }
                // If LANDSCAPE and MASTER-DETAIL dual layout
                else {
                    childFragmentManager.beginTransaction()
                            .replace(binding.detailFragmentContainer!!.id, DetailFragment())
                            .commit()
                }
            }
        })

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
        inflater.inflate(R.menu.menu_fragment_list, menu);
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
        }

        return super.onOptionsItemSelected(item)
    }

}

