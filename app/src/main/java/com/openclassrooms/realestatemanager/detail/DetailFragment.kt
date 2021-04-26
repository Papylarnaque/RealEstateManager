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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.viewmodel.EstateListViewModel

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding
    private val viewModel: EstateListViewModel by viewModels({ requireParentFragment() })
    private lateinit var detailedEstate: DetailedEstate
    private var estateKey: Long = 0
    private val pictureListAdapter = DetailPictureListAdapter(DetailPictureListener { picture ->
        // TODO() viewModel.onPictureClicked(picture)
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initBindings()
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
                            .actionDetailFragmentToCreationFragment(detailedEstate.estate!!.startTime)
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
            detailedEstate = viewModel.navigateToEstateDetail.value!!
            bindEstate()
            estateKey = detailedEstate.estate!!.startTime
        } else {
            viewModel.getEstateWithId(args.estateKey).observe(viewLifecycleOwner, { it ->
                detailedEstate = it
                bindEstate()
            })
            estateKey = args.estateKey
        }
    }

    private fun bindEstate() {
        binding.detailedEstate = this.detailedEstate
        binding.detailEstateScrollview.visibility = View.VISIBLE
        pictureListAdapter.submitList(detailedEstate.pictures as MutableList<Picture>)
        bindPois()
        binding.executePendingBindings()
    }

    private fun initBindings() {
        binding = FragmentDetailBinding.inflate(layoutInflater)

        binding.detailRecyclerviewPictures.adapter = pictureListAdapter
        val mLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.detailRecyclerviewPictures.layoutManager = mLayoutManager
    }

    private fun bindPois() {
        val estatePoisIdList = detailedEstate.estate?.estatePois!!.split("|")
        val poisStr = StringBuilder()
        viewModel.allPois().observe(viewLifecycleOwner, {
            for (poi in it) {
                if (estatePoisIdList.contains(poi.poiId.toString())) {
                    if (poisStr.isEmpty()) {
                        poisStr.append(poi.poiName)
                    } else {
                        poisStr.append(", " + poi.poiName)
                    }
                }
            }
            binding.detailPoisContent.text = poisStr
        })

    }

// TODO() Click on picture should open it full screen


}