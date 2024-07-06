package ru.netology.markers

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.netology.markers.databinding.ActivityMainBinding
import com.yandex.mapkit.map.Map
import showToast

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
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
        override fun onMapTap(map: Map, point: Point) {}

        override fun onMapLongTap(map: Map, point: Point) {
            val imageProvider = ImageProvider.fromResource(this@MainActivity, R.drawable.ic_dollar_pin)
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
        private val POINT = Point(55.751280, 37.629720)
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

        val map = mapView.mapWindow.map
        map.addTapListener(geoObjectTapListener)
        map.addInputListener(inputListener)

        map.move(POSITION)

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

}