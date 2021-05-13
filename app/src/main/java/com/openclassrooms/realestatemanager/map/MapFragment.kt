package com.openclassrooms.realestatemanager.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.api.ResultAPIMap
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.service.GeocodeService
import com.openclassrooms.realestatemanager.utils.hasPermission
import com.openclassrooms.realestatemanager.utils.showSnackbar
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel
import java.util.*

const val INITIAL_ZOOM = 12f
const val TAG = "MapFragment"

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController

    // The Fused Location Provider provides access to location APIs.
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted. Start camera preview Activity.
                binding.root.showSnackbar(
                    R.string.camera_permission_granted,
                    Snackbar.LENGTH_INDEFINITE,
                    R.string.ok
                ) {
                    requestCurrentLocation()
                }
            } else {
                // Permission request was denied.
                binding.root.showSnackbar(
                    R.string.camera_permission_denied,
                    Snackbar.LENGTH_SHORT,
                    R.string.ok)
            }
        }
    private lateinit var mMap: MapView
    private lateinit var googleMap: GoogleMap
    private val viewModel: ListDetailViewModel by viewModels()
    private var detailedEstatesList: List<DetailedEstate> = emptyList()
    private lateinit var lastUserLocation: Location
    private var cancellationTokenSource = CancellationTokenSource()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMap.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        mMap = binding.map
        mMap.onCreate(savedInstanceState)
        mMap.getMapAsync(this)

        val permissionApproved =
            requireContext().hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionApproved) {
            requestCurrentLocation()
        }

//        checkPermissions()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)

        getEstates()
        mMapSetUpClickListener()
//        enableCompassButton()
    }

    override fun onResume() {
        super.onResume()
        mMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap.onPause()
    }

    override fun onStart() {

        mMap.onStart()
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMap.onStop()
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap.onLowMemory()
    }

    // ---------------------- MANAGE MARKERS ----------------------//

    private fun getEstates() {
        viewModel.allDetailedEstates.observe(viewLifecycleOwner) {
            it.let {
                detailedEstatesList = it
                for (estate in detailedEstatesList) {
                    GeocodeService.getGeocode(estate)
                }
            }
            //            }
        }

        GeocodeService.geocodeResults.observe(
            requireActivity()
        ) { changedGeocodeResults ->
            //Do something with the changed value
            setMarkers(changedGeocodeResults)
        }

    }

    private fun setMarkers(item: List<ResultAPIMap>) {
        val newMarker = googleMap.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        item[0].geometry?.location?.lat!!,
                        item[0].geometry?.location?.lng!!
                    )
                )
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(item[0].address.toString())
//                .snippet(item[0].address.toString())
        )
        // TODO Set the estate startime as tag
        newMarker?.tag = detailedEstatesList[0].estate!!.startTime
    }

    // Handle click on marker info
    private fun mMapSetUpClickListener() {
        googleMap.setOnInfoWindowClickListener { marker: Marker ->
            Log.d(TAG, "Click on marker " + marker.tag)
            val estateKey: Long =
                Objects.requireNonNull(marker.tag) as Long

            onEstateClick(estateKey)
        }
    }

    private fun onEstateClick(estateKey: Long) {
        if (binding.detailFragmentContainer == null) {
            navController.navigate(
                MapFragmentDirections
                    .actionMapFragmentToDetailFragment(estateKey)
            )
        }
        // If LANDSCAPE and MASTER-DETAIL dual layout
        else {
            childFragmentManager.beginTransaction()
                .replace(binding.detailFragmentContainer!!.id, DetailFragment())
                .commit()
        }
    }


    // ---------------------- COMPASS BUTTON ----------------------//
    // Permission managed before this call

    @SuppressLint("MissingPermission")
    fun enableCompassButton() {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
    }


    private fun zoomOnUserLocation() {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lastUserLocation.latitude, lastUserLocation.longitude),
                INITIAL_ZOOM
            )
        )
    }

    // Location permission

    /**
     * Gets current location.
     * Note: The code checks for permission before calling this method, that is, it's never called
     * from a method with a missing permission. Also, I include a second check with my extension
     * function in case devs just copy/paste this code.
     */
    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation() {
        Log.d(TAG, "requestCurrentLocation()")
        if (requireContext().hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Returns a single current location fix on the device.
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                val result = if (task.isSuccessful && task.result != null) {
                    val result: Location = task.result
                    lastUserLocation = result
                    "Location (success): ${result.latitude}, ${result.longitude}"
                } else {
                    val exception = task.exception
                    "Location (failure): $exception"
                }

                Log.d(TAG, "getCurrentLocation() result: $result")
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        zoomOnUserLocation()
        return false
    }








    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            binding.root.showSnackbar(
                R.string.camera_permission_available,
                Snackbar.LENGTH_INDEFINITE,
                R.string.ok
            ) {
                requestCurrentLocation()
            }
        } else requestLocationPermission()
    }

    /**
     * Requests the [android.Manifest.permission.CAMERA] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            binding.root.showSnackbar(
                R.string.camera_access_required,
                Snackbar.LENGTH_INDEFINITE,
                R.string.ok
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        } else {
            // You can directly ask for the permission.
            binding.root.showSnackbar(
                R.string.camera_permission_not_available,
                Snackbar.LENGTH_LONG,
                R.string.ok
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}
