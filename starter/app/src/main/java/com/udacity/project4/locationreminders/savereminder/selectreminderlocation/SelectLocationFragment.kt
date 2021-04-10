package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.res.Resources
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.LocationUtility
import com.udacity.project4.utils.observeInLifecycle
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.*


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback,EasyPermissions.PermissionCallbacks
{

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
     var locationCallback: LocationCallback? =null
    lateinit var mapFragment: SupportMapFragment
    lateinit var geocoder: Geocoder

    var location: Location? = null
    var POI:String? = null


    @InternalCoroutinesApi
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View?
    {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        //testing sending one time events with coroutines channels+Flow to be used in future projects
        _viewModel.eventsFlow.onEach {
            Toast.makeText(this.requireContext(),it,Toast.LENGTH_SHORT).show()
        }.observeInLifecycle(this)




        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this.requireActivity())
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


//        TODO: add the map setup implementation
        mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@SelectLocationFragment)


//        TODO: call this function after the user confirms on the selected location
        binding.saveButton.setOnClickListener {
             onLocationSelected()
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()
        geocoder = Geocoder(this.requireContext())






    }


    private fun onLocationSelected()
    {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    if (location != null)
    {
        lifecycleScope.launch(Dispatchers.Main) {
            var itemLocation = ""
            withContext(Dispatchers.IO) {
                itemLocation=
                    try
                    {
                        geocoder.getFromLocation(location?.latitude!!, location?.longitude!!, 1).first()
                            .run {
                                val sb = StringBuilder()
                                for (i in 0 until this.maxAddressLineIndex)
                                {
                                    sb.append(this.getAddressLine(i)).append("\n")
                                }
                                //sb.append(this.locality).append("\n")
                                //sb.append(this.thoroughfare).append("\n")
                                sb.append(POI ?:this.locality ?: "NotAvailable" )
                                //sb.append(this.countryName)
                                sb.toString()
                            }
                    } catch (e: Exception)
                    {

                        Log.d("adressList", "onLocationSelected: $e.localizedMessage")
                        e.localizedMessage!!
                    }

            }



            _viewModel.reminderSelectedLocationStr.postValue(itemLocation)
            _viewModel.latitude.postValue(location?.latitude)
            _viewModel.longitude.postValue(location?.longitude)

            _viewModel.navigationCommand.postValue(NavigationCommand.Back)


        }
    }





    }

    override fun onResume()
    {
        super.onResume()
        getUserLocation()

        Log.d("onStart", " started")
    }

    override fun onPause()
    {
        super.onPause()
       // stopLocationUpdates()
        Log.d("Pause", "onPause: ")
    }

    private fun stopLocationUpdates()
    {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.normal ->
            {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.hybrid_map ->
            {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID

            }
            R.id.satellite_map ->
            {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE

            }
            R.id.terrain_map ->
            {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN

            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true

    }

    override fun onMapReady(p0: GoogleMap?)
    {

        if (p0 != null)
        {
            map = p0
            //adding mapstyle
            setMapStyle(map)
            onPoiPress(map)

            map.setOnMapLongClickListener {

                it?.let {
                    handleMapLongClick(it)
                }
            }

        }

    }

    @SuppressLint("MissingPermission")
    fun getUserLocation()
    {
        if (LocationUtility.hasLocationPermissions(this.requireContext()))
        {

            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                smallestDisplacement = 100f
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                Log.d(null, "getuserlocation: entered1")
            }

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(this.requireActivity())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {

                val flag = it.locationSettingsStates?.isLocationUsable
                if (flag == true)
                {
                    locationCallback = object : LocationCallback()
                    {
                        override fun onLocationResult(locationResult: LocationResult)
                        {
                            location = locationResult.lastLocation
                            location?.let {
                                map.clear()
                                setupMap(location!!)
                            }

                        }
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
                }

            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException)
                {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    Log.d(null, "setuserlocation: location failure")
                    try
                    {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(this.requireActivity(), 0x1)
                    } catch (sendEx: IntentSender.SendIntentException)
                    {
                        // Ignore the error.
                    }
                }
            }


        } else
        {
            requestPermissions()
        }


    }

    @SuppressLint("MissingPermission")
    private fun setupMap(location: Location)
    {
       // map.clear()
        map.isMyLocationEnabled = true
        val lat = location.latitude
        val lang = location.longitude
        Log.d("location", "setupMap: $lat + $lang")
        if (lat != null && lang != null)
        {

            val currentLocation = LatLng(lat, lang)
            val zoom = 18f
            // zoom to the user location after taking his permission
            // put a marker to location that the user selected
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom))
            map.addMarker(MarkerOptions().position(currentLocation))
            _viewModel.mapToastTriggered()

        }


    }

    private fun onPoiPress(map: GoogleMap)
    {

        map.setOnPoiClickListener {
            map.clear()
            POI = it.name.filter {
                Character.UnicodeBlock.of(it) == Character.UnicodeBlock.of('A')

            }.trimIndent()
            if (POI.isNullOrEmpty())
            {
                POI = it.name
            }
            map.addMarker(MarkerOptions().position(it.latLng)
                    .title(it.name))
                    .showInfoWindow()
        }
    }

    private fun requestPermissions()
    {
        if (LocationUtility.hasLocationPermissions(requireContext()))
        {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app.",
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else
        {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app.",
                    REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>)
    {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms.toString()))
        {
            SettingsDialog.Builder(this.requireContext()).build().show()
        } else
        {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>)
    {
        return
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun setMapStyle(map: GoogleMap)
    {
        try
        {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.requireContext(),
                            R.raw.maps_style
                    )
            )

            if (!success)
            {
                Log.e(null, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException)
        {
            Log.e(null, "Can't find style. Error: ", e)
        }
    }

    private fun addMarker(latLng: LatLng)
    {
        val markerOptions = MarkerOptions().position(latLng)
        map.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Float)
    {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        map.addCircle(circleOptions)
    }

    private fun handleMapLongClick(latLng: LatLng)
    {
        map.clear()
        addMarker(latLng)
        addCircle(latLng, 200f)
        location?.let {
            it.latitude = latLng.latitude
            it.longitude = latLng.longitude
        }

    }


}

private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34


