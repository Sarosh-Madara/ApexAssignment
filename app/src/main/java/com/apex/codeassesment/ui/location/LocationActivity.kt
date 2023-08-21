package com.apex.codeassesment.ui.location

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.R
import com.apex.codeassesment.databinding.ActivityLocationBinding
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationActivity : AppCompatActivity() {

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityLocationBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val latitudeRandomUser = intent.getDoubleExtra("user-latitude-key", 0.0)
    val longitudeRandomUser = intent.getDoubleExtra("user-longitude-key", 0.0)

    binding.locationRandomUser.text = getString(
      R.string.location_random_user,
      latitudeRandomUser.toString(),
      longitudeRandomUser.toString()
    )

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    binding.locationCalculateButton.setOnClickListener {
      if (hasLocationPermission()) {
        calculateDistance(latitudeRandomUser, longitudeRandomUser)
      } else {
        requestLocationPermission()
      }
    }
  }

  private fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(
      this,
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
      LOCATION_PERMISSION_REQUEST_CODE
    )
  }

  private fun calculateDistance(latitudeRandomUser: Double, longitudeRandomUser: Double) {
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    fusedLocationClient.lastLocation
      .addOnSuccessListener { location: Location? ->
        if (location != null) {
          val distance = calculateDistance(
            location.latitude, location.longitude,
            latitudeRandomUser, longitudeRandomUser
          )

          Toast.makeText(this, "Distance b/w two coordinates is ${distance}", Toast.LENGTH_SHORT).show()
        } else {
          Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show()
        }
      }
      .addOnFailureListener {
        Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show()
      }
  }

  private fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
  ): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
  }

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 123
  }
}

