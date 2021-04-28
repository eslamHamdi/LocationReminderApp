package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent




class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {

//TODO: implement the onReceive method to receive the geofencing events at the background



        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError())
        {
            val errorMessage =  geofencingEvent.errorCode
            Log.e("Broadcast", "errorMessage")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            Log.d("Broadcast", "GeofenceEntered")
            val serviceIntent = Intent(context,GeofenceTransitionsJobIntentService::class.java)
            val requestId  = when
            {
                geofencingEvent.triggeringGeofences.isNotEmpty() ->
                    geofencingEvent.triggeringGeofences[0].requestId
                else ->
                {
                    Log.e("Broadcast", "No Geofence Trigger Found! Abort mission!")
                    return
                }

            }

            serviceIntent.putExtra("Geo_trigger",requestId)
            GeofenceTransitionsJobIntentService.enqueueWork(context,serviceIntent)

            }



        }
    }
