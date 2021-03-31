package com.openclassrooms.realestatemanager.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.openclassrooms.realestatemanager.MainActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Handle the back button event
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            NavHostFragment.findNavController(requireParentFragment())
                    .navigate(R.id.action_detailFragment_to_listFragment)
            switchBackButton(false)
        }


        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_detail,
                container,
                false
        )

        setHasOptionsMenu(true)
        // TODO() Change the call to backbutton
        switchBackButton(true)

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        return binding.root
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


    // TODO() Fix DetailFragment alone when rotating from portrait to landscape when on detail view
//    override fun onResume() {
//        super.onResume()
//        // true only in landscape
//        if (resources.getBoolean(R.bool.is_landscape)) {
//            NavHostFragment.findNavController(requireParentFragment())
//                    .navigate(R.id.action_detailFragment_to_listFragment)
//        }
//    }

}