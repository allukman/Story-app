package com.karsatech.storyapp.ui.map

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.karsatech.storyapp.R
import com.karsatech.storyapp.data.remote.response.DetailStory
import com.karsatech.storyapp.data.remote.retrofit.ApiConfig
import com.karsatech.storyapp.data.remote.retrofit.ApiService
import com.karsatech.storyapp.databinding.ActivityMapsBinding
import com.karsatech.storyapp.utils.withDateFormat

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var service: ApiService

    private val boundsBuilder = LatLngBounds.builder()

    private val bottomSheetView by lazy { findViewById<ConstraintLayout>(R.id.bottomSheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var myMarker: DetailStory? = null
    private val mapsViewModel by viewModels<MapsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ApiConfig.getApiClient(this)!!.create(ApiService::class.java)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

        setBottomSheetVisibility(false)

        settingViewModel()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setUiMapSetting()
        setMapStyle()
        subscribeMainViewModel()
        mapClickListener()
    }

    private fun mapClickListener() {
        mMap.setOnMarkerClickListener { marker ->
            onMarkerClicked(marker)
            false
        }

        mMap.setOnMapClickListener {
            setBottomSheetVisibility(false)
            showView(false)
        }
    }

    private fun onMarkerClicked(marker: Marker) {
        val story = marker.tag as? DetailStory
        if (story != null) {
            myMarker = story

            bottomSheetView.findViewById<TextView>(R.id.tv_name).text = story.name
            bottomSheetView.findViewById<TextView>(R.id.tv_date).text = " -  ${story.createdAt.withDateFormat()}"
            bottomSheetView.findViewById<TextView>(R.id.tv_desc).text = story.description

            setBottomSheetVisibility(true)
            showView(true)
        } else {
            myMarker = null
            setBottomSheetVisibility(false)
            showView(false)
        }
    }

    private fun showView(show: Boolean) {
        bottomSheetView.findViewById<TextView>(R.id.tv_name).isVisible = show
        bottomSheetView.findViewById<TextView>(R.id.tv_date).isVisible = show
        bottomSheetView.findViewById<TextView>(R.id.tv_desc).isVisible = show
    }

    private fun subscribeMainViewModel() {
        mapsViewModel.stories.observe(this) { stories ->
            Log.d(TAG, stories.toString())

            stories.forEach {
                Log.d(TAG, "Stories : $it")
            }
            setMarker(stories)
        }
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        if (isVisible) {
            bottomSheetView.visibility = View.VISIBLE
        }
        val updatedState =
            if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun setMarker(data: List<DetailStory>) {
        val filteredStory = data.filter { story ->
            story.lat != null && story.lon != null
        }

        filteredStory.forEach { story ->
            val latLng = LatLng(story.lat!!, story.lon!!)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .icon(convertDrawableToBitmapDescriptor(this, R.drawable.location, 30f))
            )
            marker?.tag = story
            boundsBuilder.include(latLng)
            Log.d(TAG, "filtered story : $story")
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                100
            )
        )
    }

    private fun convertDrawableToBitmapDescriptor(context: Context, drawableId: Int, dpSize: Float): BitmapDescriptor? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId, options)

        val pxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, context.resources.displayMetrics).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, pxSize, pxSize, true)

        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private fun setUiMapSetting() {
        mMap.setPadding(24,24,24,120)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
    }

    private fun settingViewModel() {
        mapsViewModel.apply {
            setService(service)
            getAllStories()
        }
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
    }
}