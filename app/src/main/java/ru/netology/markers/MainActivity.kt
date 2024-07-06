package ru.netology.markers

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import showToast

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var map: Map

    private val locationListener = object : LocationListener {
        override fun onLocationUpdated(p0: Location) {
            map.move(
                CameraPosition(p0.position, 17.0f, 150.0f, 30.0f),
                Animation(Animation.Type.SMOOTH, 5F),
                null
            )
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) {
            showToast("Перемещаемся в точку вашей локации.")
        }

    }

    val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                MapKitFactory.getInstance().createLocationManager()
                    .requestSingleUpdate(locationListener)
            }
        }


    private val placemarkTapListener = MapObjectTapListener { mapObject, point ->
        showToast("Tapped the point (${mapObject.userData})")

        true
    }

    val geoObjectTapListener = object : GeoObjectTapListener {
        override fun onObjectTap(event: GeoObjectTapEvent): Boolean {
            showToast("${event.geoObject.name}")
            return true
        }
    }

    val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {

        }

        override fun onMapLongTap(map: Map, point: Point) {
            val imageProvider =
                ImageProvider.fromResource(this@MainActivity, R.drawable.ic_dollar_pin)
            val placemarkObject = map.mapObjects.addPlacemark().apply {
                geometry = point
                setIcon(imageProvider)
                setTextStyle(TEXT_STYLE)
                userData = "Важное место"
                setText("Важное место")
            }
            placemarkObject.addTapListener(placemarkTapListener)
        }
    }

    companion object {
        private val POINT = Point(55.704473, 37.624700)
        private val POSITION = CameraPosition(POINT, 17.0f, 150.0f, 30.0f)
        private val TEXT_STYLE = TextStyle().apply {
            placement = TextStyle.Placement.RIGHT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapview)
        val mapkitVersionView = findViewById<LinearLayout>(R.id.mapkit_version)
        val mapkitVersionTextView =
            mapkitVersionView.findViewById<TextView>(R.id.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        checkPermission()

        map = mapView.mapWindow.map
        map.apply {
            addTapListener(geoObjectTapListener)
            addInputListener(inputListener)
            move(POSITION, Animation(Animation.Type.SMOOTH, 5F), null)
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun checkPermission() {
        val permissions = android.Manifest.permission.ACCESS_FINE_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                this,
                permissions
            ) == PackageManager.PERMISSION_GRANTED -> {
                MapKitFactory.getInstance().createLocationManager()
                    .requestSingleUpdate(locationListener)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, permissions
            ) -> {
                showToast("Доступ к геолокации запрещен.")
            }

            else -> {
                requestPermissionLauncher.launch(permissions)
            }
        }
    }

}