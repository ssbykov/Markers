package ru.netology.markers.activity

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
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
import ru.netology.markers.BuildConfig
import ru.netology.markers.R
import ru.netology.markers.databinding.FragmentMapsBinding
import ru.netology.markers.utils.showToast
import ru.netology.markers.viewmodel.MapsVeiwModel

class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: MapView
    private lateinit var map: Map
//    private val viewModel: MapsVeiwModel by viewModels(
//        ownerProducer = ::requireParentFragment
//    )

    private val locationListener = object : LocationListener {
        override fun onLocationUpdated(p0: Location) {
            map.move(
                CameraPosition(p0.position, 17.0f, 150.0f, 30.0f),
                Animation(Animation.Type.SMOOTH, 5F),
                null
            )
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) {
            requireContext().showToast(getString(R.string.move_to_location))
        }

    }

    val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                MapKitFactory.getInstance().createLocationManager()
                    .requestSingleUpdate(locationListener)
            }
        }


    private val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        requireContext().showToast("Tapped the point (${mapObject.userData})")
        true
    }

    val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {

        }

        override fun onMapLongTap(map: Map, point: Point) {
            val imageProvider =
                ImageProvider.fromResource(context, R.drawable.ic_dollar_pin)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        MapKitFactory.initialize(requireContext())

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = requireActivity().findViewById(R.id.mapview)
        val mapkitVersionView = requireActivity().findViewById<LinearLayout>(R.id.mapkit_version)
        val mapkitVersionTextView =
            mapkitVersionView.findViewById<TextView>(R.id.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        checkPermission()

        map = mapView.mapWindow.map
        map.apply {
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


    companion object {
        private val POINT = Point(55.704473, 37.624700)
        private val POSITION = CameraPosition(POINT, 17.0f, 150.0f, 30.0f)
        private val TEXT_STYLE = TextStyle().apply {
            placement = TextStyle.Placement.RIGHT
        }

        @JvmStatic
        fun newInstance() = MapsFragment()
    }

    private fun checkPermission() {
        val permissions = android.Manifest.permission.ACCESS_FINE_LOCATION
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permissions
            ) == PackageManager.PERMISSION_GRANTED -> {
                MapKitFactory.getInstance().createLocationManager()
                    .requestSingleUpdate(locationListener)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), permissions
            ) -> {
                requireContext().showToast(getString(R.string.access_geo_prohibited))
            }

            else -> {
                requestPermissionLauncher.launch(permissions)
            }
        }
    }
}