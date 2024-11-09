package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;

import com.google.android.gms.location.Priority;

public class LocationSenderService extends Service {
    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel("Location_channel", "locChannel", importance);
            mChannel.setDescription("Sending your location to server");
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);

            Notification notification = new Notification.Builder(this, "Location_channel").setSmallIcon(R.drawable.share_location).build();
            startForeground(1, notification);
        }

    }

    public IBinder onBind(Intent intent){
        return null;
    }
       /* private fun createNotification(): Notification {
        val channelId = "location_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Location Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return Notification()
    }*/

    /*private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                sendLocationToServer(location)
            }
        }
    }

    private fun sendLocationToServer(location: Location) {
        coroutineScope.launch {
            // Replace with your server's API endpoint
            val url = "https://yourserver.com/api/location"
            val locationData = mapOf("latitude" to location.latitude, "longitude" to location.longitude)

            // Implement your HTTP request here (e.g., using Retrofit or HttpURLConnection)
            // For example, use Retrofit to send the data to the server
        }
    }*/

    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private val coroutineScope = CoroutineScope(Dispatchers.IO)
}
