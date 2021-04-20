package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.viewmodel.EstateListViewModel

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: EstateListViewModel by viewModels({ requireParentFragment() })
    private lateinit var estate: Estate
    private val pictureListAdapter = PictureListAdapter(PictureListener { picture ->
        // TODO() viewModel.onPictureClicked(picture)
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater)

        binding.detailRecyclerviewPictures.adapter = pictureListAdapter
        binding.detailRecyclerviewPictures.layoutManager = LinearLayoutManager(context)

        // TODO() Put pictures in recyclerview

        getEstate()

        // override back navigation from detail fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (this@DetailFragment.findNavController().currentDestination?.id == R.id.detailFragment) {
                NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_detailFragment_to_listFragment)
            }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
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
                //Open CreationFragment for Edition
                Log.i("DetailFragment", "Click on edit an estate")
                NavHostFragment.findNavController(this)
                    .navigate(
                        DetailFragmentDirections
                            .actionDetailFragmentToCreationFragment(estate.startTime)
                    )
            }

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
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
        binding.detailEstateScrollview.visibility = View.VISIBLE
        binding.executePendingBindings()
    }


    // TODO() Click on picture should open it full screen


}