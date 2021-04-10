package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.LocationUtility
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject


class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q
    private val GEOFENCE_RADIUS = 200f
    private var geofencingClient: GeofencingClient? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        geofencingClient = LocationServices.getGeofencingClient(this.requireActivity())
        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
val reminder = ReminderDataItem(title,description,location,latitude, longitude)
//            TODO: use the user entered reminder details to:

//             1) add a geofencing request
            if (!title.isNullOrEmpty() && !description.isNullOrEmpty() && !location.isNullOrEmpty())
            {
                addGeofence(LatLng(latitude!!,longitude!!),GEOFENCE_RADIUS, reminder.id)
            }

//             2) save the reminder to the local db
            _viewModel.validateAndSaveReminder(reminder)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng: LatLng, radius: Float,ID:String)
    {
        val geofence: Geofence? = LocationUtility.getGeofence(ID , latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest: GeofencingRequest? = LocationUtility.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent = LocationUtility.getPendingIntent(this.requireContext())

        if (foregroundAndBackgroundLocationPermissionApproved())
        {
            geofencingClient?.addGeofences(geofencingRequest!!, pendingIntent)


                    ?.addOnSuccessListener { Log.d(null, "onSuccess: Geofence Added...") }
                    ?.addOnFailureListener { e ->
                        val errorMessage: String? = LocationUtility.getErrorString(e)
                        Log.d(null, "onFailure: $errorMessage")
                    }

        }else
        {
            return
        }


    }

    @SuppressLint("InlinedApi")
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val forground  =(
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION))
        val background =  (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION))

        if (runningQOrLater)
        {
            return forground && background
        }

        return forground

    }
}
