package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.viewmodel.EstateListViewModel

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: EstateListViewModel by viewModels({ requireParentFragment() })
    private lateinit var estate: Estate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setUpBackNavigation()
        getEstate()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_detail, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //get item id to handle item clicks
        when (item.itemId) {

            R.id.edit_estate -> {
                // Disable back button
                switchBackButton(false)
                //Open CreationFragment
                Log.i("DetailFragment", "Click on edit an estate")
                Toast.makeText(activity, "Edit an estate", Toast.LENGTH_SHORT).show()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_detailFragment_to_creationFragment)
            }

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                switchBackButton(false)
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        setUpBackButton()
        super.onResume()
    }

    private fun getEstate() {
        if (viewModel.navigateToEstateDetail.value != null) {
            this.estate = viewModel.navigateToEstateDetail.value!!
            bindEstate()
        } else {
            viewModel.getEstateWithId(args.estateKey).observe(viewLifecycleOwner, { estate ->
                this.estate = estate
                bindEstate()
            })
        }
    }

    private fun bindEstate() {
        binding.estate = this.estate
        binding.executePendingBindings()
        binding.detailNotSelected.visibility = View.INVISIBLE
        binding.detailEstateScrollview.visibility = View.VISIBLE
    }


    /**
     * Handle back navigation purpose
     */
    private fun setUpBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (this@DetailFragment.findNavController().currentDestination?.id == R.id.detailFragment) {
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_detailFragment_to_listFragment)
                switchBackButton(false)
            }
        }
        setUpBackButton()
    }

    /**
     * Handle back button depending on orientation and currentDestination
     */
    private fun setUpBackButton() {
        val backNavBoolean = !(requireActivity() as MainActivity).twoPane &&
                this.findNavController().currentDestination?.id == R.id.detailFragment
        switchBackButton(backNavBoolean)
    }

    /**
     * Handle back button on call
     */
    private fun switchBackButton(boolean: Boolean) {
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(boolean)
    }


}