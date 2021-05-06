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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.api.ResultAPIMap
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment
import com.openclassrooms.realestatemanager.service.GeocodeService
import com.openclassrooms.realestatemanager.utils.KUtil
import com.openclassrooms.realestatemanager.utils.showSnackbar
import com.openclassrooms.realestatemanager.viewmodel.EstateListViewModel
import java.util.*

open class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private val TAG = "MapFragment"
    private val INITIAL_ZOOM = 12f
    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mMap: MapView
    private lateinit var googleMap: GoogleMap
    private val viewModel: EstateListViewModel by viewModels()
    private var detailedEstatesList: List<DetailedEstate> = emptyList()
    private lateinit var lastUserLocation: Location
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                lastUserLocation = location
            }
        }
    }

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
//        val view = inflater.inflate(R.layout.fragment_map, container, false)

//        mMap = view.findViewById(R.id.map) as MapView
        mMap = binding.map
        mMap.onCreate(savedInstanceState)
        mMap.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        return view
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

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
        enableCompassButton(googleMap)
        mMapSetUpClickListener()
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
                    //                    if (estate.estate?.estateLatlng !=null){
                    //                        setMarkers(estate)
                    //                    } else {
                    // Request each estate latlng
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
//                googleMap, LatLng(
//                    changedGeocodeResults[0].geometry?.location?.lat!!,
//                    changedGeocodeResults[0].geometry?.location?.lng!!
//                )
//            )
        }

    }

    private fun setMarkers(item: List<ResultAPIMap>) {
//    private fun setMarkers(item: HashMap<Long, ResultAPIMap>) {
//        for (estate in item) {
        val newMarker = googleMap.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        item[0].geometry?.location?.lat!!,
                        item[0].geometry?.location?.lng!!
                    )
//                        estate.value.geometry?.location?.lat!!,
//                        estate.value.geometry?.location?.lat!!)
                )
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(item[0].address.toString())
                .snippet(item[0].address.toString())
        )
        newMarker?.tag = detailedEstatesList[0].estate!!.startTime
//        }
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
    open fun enableCompassButton(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
    }

    override fun onMyLocationButtonClick(): Boolean {
        requestLocationPermission()
        getLastLocation()
        return false
    }

    // Last fused Location

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(requireActivity(),
            OnSuccessListener { location ->
                if (location == null) {
                    // TODO requestCurrentLocation()
                    Log.w(TAG, "onSuccess:null")
                    return@OnSuccessListener
                }
                Log.i(TAG, "onSuccess lastLocation: ${location.latitude}, ${location.longitude}")
                lastUserLocation = location
                zoomOnUserLocation()
            })?.addOnFailureListener(requireActivity()) { e ->
            Log.w(TAG, "getLastLocation:onFailure", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
//        fusedLocationClient?.requestLocationUpdates(locationRequest,
//            locationCallback,
//            Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted. Start camera preview Activity.
                KUtil.infoSnackBar(
                    requireView(), "Location permission granted"
                )
//                getLastLocation()
            } else {
                // Permission request was denied.
                KUtil.infoSnackBar(
                    requireView(), "Location permission DENIED"
                )
                requestLocationPermission()
            }
        }


    /**
     * Requests the [android.Manifest.permission.CAMERA] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private fun requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            view?.showSnackbar(
                R.string.camera_access_required,
                Snackbar.LENGTH_INDEFINITE,
                R.string.ok
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            // You can directly ask for the permission.
            view?.showSnackbar(
                R.string.camera_permission_not_available,
                Snackbar.LENGTH_LONG,
                R.string.ok
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

//            view?.showSnackbar(R.string.permission_rationale, android.R.string.ok,
//                view.OnClickListener {
//                    // Request permission
//                    ActivityCompat.requestPermissions(requireActivity(),
//                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                        REQUEST_PERMISSIONS_REQUEST_CODE)
//                })

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }


}
