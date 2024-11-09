package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import okhttp3.WebSocket
import okhttp3.Response
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class LocationSenderService : Service() {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    override fun onCreate() {
        super.onCreate()

        val locationManager = LocationManager(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("Location_channel", "locChannel", importance)
            mChannel.description = "Sending your location to server"
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val notification = NotificationCompat.Builder(this, "Location_channel")
                .setSmallIcon(R.drawable.share_location)
                .setContentText("Location: ..\$latitude / ..\$longitude")
                .build()
            startForeground(1, notification)
            val options = IO.Options().apply {
                transports = arrayOf("websocket")
            }

            val socket: Socket = IO.socket("https://sonic-sync-78daad0a1d18.herokuapp.com/", options)
            socket.connect()

            scope.launch {
                locationManager.trackLocation().collect { location ->
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    val updatedNotification = NotificationCompat.Builder(this@LocationSenderService, "Location_channel")
                        .setSmallIcon(R.drawable.share_location)
                        .setContentText("Location: $latitude / $longitude")
                        .build()
                    notificationManager.notify(1, updatedNotification)
                    val gpsData = JSONObject().apply {
                        put("user_id", "0")  // Replace with actual user_id
                        put("latitude", location.latitude)
                        put("longitude", location.longitude)
                        put("timestamp", System.currentTimeMillis())
                    }

                    socket.emit("gps_data", gpsData)
                    println("GPS data emitted: $gpsData")

                }
            }

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    } /* private fun createNotification(): Notification {
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
