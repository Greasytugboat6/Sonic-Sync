package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var user : String? = null;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myWebView: WebView = findViewById(R.id.webView)
        myWebView.webViewClient = RedirectViewer()
        myWebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        myWebView.addJavascriptInterface(UserIDInterface(this), "UserIDInterface")
        myWebView.loadUrl("https://sonic-sync-78daad0a1d18.herokuapp.com/")

        startService(Intent(this, LocationSenderService::class.java).apply {})
        val locationManager = LocationManager(this)
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to get the location?")
            .setPositiveButton("Yes") { _, _ ->
                locationManager.getLocation { latitude, longitude ->
                    val locationText = "Location: ..$latitude / ..$longitude"
                    Snackbar.make(binding.root, locationText, Snackbar.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}