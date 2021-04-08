package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.viewmodel.EstateListViewModel

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: EstateListViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        // TODO() Change the call to backbutton
//        switchBackButton(true)
        // Handle the back button event
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_detailFragment_to_listFragment)
            switchBackButton(false)
        }


        if (viewModel.navigateToEstateDetail.value != null) {
            binding.estate = viewModel.navigateToEstateDetail.value
            binding.executePendingBindings()
            showEstate()
        } else if (args.estateKey != null) {
            viewModel.getEstateWithId(args.estateKey).observe(viewLifecycleOwner, { estate ->
                binding.estate = estate
                binding.executePendingBindings()
                showEstate()
            })
        } else {
            hideEstate()
        }

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_detail, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handle item clicks of menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //get item id to handle item clicks
        when (item.itemId) {

            R.id.edit_estate -> {
                // Disable back button
                switchBackButton(false)
                //Open CreationFragment
                Log.i("DetailFragment", "Click on edit an estate")
                Toast.makeText(activity, "Edit an estate", Toast.LENGTH_SHORT).show()
                NavHostFragment.findNavController(this).navigate(R.id.action_detailFragment_to_creationFragment)
            }

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                switchBackButton(false)
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun switchBackButton(boolean: Boolean) {
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(boolean)
    }


    //    // TODO() Fix DetailFragment alone when rotating from portrait to landscape when on detail view
//    override fun onResume() {
//        super.onResume()
//        // true only in landscape
//        if (resources.getBoolean(R.bool.is_landscape)) {
//            if (args == null) {
//                hideEstate()
//            } else
//            NavHostFragment.findNavController(requireParentFragment())
//                    .navigate(R.id.listFragment)
//        }
    //        else {
//            if (args == null) {
//                hideEstate()
//            } else
//                NavHostFragment.findNavController(requireParentFragment())
//                        .navigate(R.id.detailFragment)
//        }
//    }


    private fun hideEstate() {
        binding.detailNotSelected.visibility = View.VISIBLE
        binding.detailEstateScrollview.visibility = View.INVISIBLE
    }

    private fun showEstate() {
        binding.detailNotSelected.visibility = View.INVISIBLE
        binding.detailEstateScrollview.visibility = View.VISIBLE
    }


}