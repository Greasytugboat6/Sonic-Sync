package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import io.socket.client.IO
import io.socket.client.Socket

class LocationSenderService : Service() {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )
    private var serviceIntent: Intent? = null



    override fun onCreate() {
        super.onCreate()

        val locationManager = LocationManager(applicationContext)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("Location_channel", "locChannel", importance)
            mChannel.description = "Sending your location to server"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val notification = NotificationCompat.Builder(this, "Location_channel")
                .setSmallIcon(R.drawable.share_location)
                .setContentText("Campus Companion is running\nSwipe to stop")
                .build()
            startForeground(1, notification)
            val options = IO.Options().apply {
                transports = arrayOf("websocket")
            }

            val socket: Socket = IO.socket("https://sonic-sync-78daad0a1d18.herokuapp.com/", options)
            socket.connect()

            scope.launch {
                locationManager.trackLocation().collect { location ->
                    val userId = UserHolder.user
                    Log.d("User ID", "$userId")
                    if (userId != null) {
                        val gpsData = JSONObject().apply {
                            put("user_id", userId)
                            put("latitude", location.latitude)
                            put("longitude", location.longitude)
                            put("timestamp", System.currentTimeMillis())
                        }

                        socket.emit("gps_data", gpsData)
                        Log.d("GPS data emitted", "$gpsData")
                    }
                    else {
                        Log.d("Error", "User ID is null")
                    }

                }
            }

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
