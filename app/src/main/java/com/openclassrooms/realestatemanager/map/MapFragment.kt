package com.openclassrooms.realestatemanager.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.api.EstateGeocode
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.service.GeocodeService
import com.openclassrooms.realestatemanager.utils.Permissions.isPermissionGranted
import com.openclassrooms.realestatemanager.utils.Permissions.requestPermission
import com.openclassrooms.realestatemanager.viewmodel.ListDetailViewModel
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {


    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mapView: MapView
    private var permissionDenied = false
    private lateinit var googleMap: GoogleMap
    private val viewModel: ListDetailViewModel by viewModels({ requireParentFragment() })
    private var detailedEstatesList: List<DetailedEstate> = emptyList()


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)

        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
        zoomOnNY()
        googleMap.cameraPosition.target
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)

        getEstates()
        mMapSetUpClickListener()
        enableMyLocation()
    }

    private fun zoomOnNY() {
        val cameraPosition = CameraPosition.Builder()
            .target(NYC_LATLNG) // center to NY city
            .zoom(INITIAL_ZOOM) // Sets the zoom
            .build() // Creates a CameraPosition from the builder

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private fun enableMyLocation() {
        if (!::googleMap.isInitialized) return
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                it?.let {
                    CameraPosition.Builder()
                        .target(LatLng(it.latitude, it.longitude))
                        .zoom(INITIAL_ZOOM)
                        .build()
                }
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            requestPermission(
                requireActivity() as AppCompatActivity, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, false
            )
        }
        // [END maps_check_location_permission]
    }

    override fun onResume() {
        super.onResume()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            permissionDenied = false
        } else {
            mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStart() {

        mapView.onStart()
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
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
        }

//        GeocodeService.geocodeResults.observe(
//            requireActivity()
//        ) { changedGeocodeResults ->
//            setMarkers(changedGeocodeResults)
//        }

        GeocodeService.estateGeocode.observe(
            requireActivity()
        ) { estateGeocode ->
            setMarkers(estateGeocode)
        }

    }

    // ---------------------- MARKERS SETUP ----------------------//

    private fun setMarkers(item: EstateGeocode) {
        val newMarker = googleMap.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        item.lat!!,
                        item.lng!!
                    )
                )
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(item.address.toString())
        )
        newMarker?.tag = item.startTime
    }

    // ---------------------- MARKER CLICK ----------------------//

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
        navController.navigate(
            MapFragmentDirections
                .actionMapFragmentToDetailFragment(estateKey)
        )
    }


    // ---------------------- COMPASS BUTTON ----------------------//

    @SuppressLint("MissingPermission")
    override fun onMyLocationButtonClick(): Boolean {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    // ---------------------- PERMISSIONS ----------------------//

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "MapFragment"
        private const val INITIAL_ZOOM = 10f
        private val NYC_LATLNG = LatLng(40.7805722,-73.99308)
    }
}
