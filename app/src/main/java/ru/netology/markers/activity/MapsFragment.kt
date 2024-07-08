package ru.netology.markers.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
import ru.netology.markers.R
import ru.netology.markers.databinding.FragmentMapsBinding
import ru.netology.markers.dto.MapObject
import ru.netology.markers.utils.compareLocations
import ru.netology.markers.utils.showToast
import ru.netology.markers.viewmodel.MapsVeiwModel

class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: MapView
    private lateinit var map: Map
    private val viewModel: MapsVeiwModel by activityViewModels()

    private val locationListener = object : LocationListener {
        override fun onLocationUpdated(p0: Location) {
            viewModel.setCurrtntLocation(p0.position)
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) {}

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
                val mapObject = MapObject(
                    latitude = point.latitude,
                    longitude = point.longitude,
                    name = "Важное место",
                    description = "Описание важного места"
                )
                viewModel.save(mapObject)
            }
            placemarkObject.addTapListener(placemarkTapListener)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater)
        MapKitFactory.initialize(requireContext())

        viewModel.currtntLocation.observe(viewLifecycleOwner) { position ->
            move(position, getString(R.string.move_to_location))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapview
        val mapkitVersionTextView = binding.mapkitVersion.mapkitVersionValue
        mapkitVersionTextView.text = MapKitFactory.getInstance().version


        map = mapView.mapWindow.map
        map.addInputListener(inputListener)

        binding.location.setOnClickListener {
            setLocation()
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
        private val TEXT_STYLE = TextStyle().apply {
            placement = TextStyle.Placement.RIGHT
        }

        @JvmStatic
        fun newInstance() = MapsFragment()
    }

    private fun setLocation() {
        if (checkPermission()) {
            Snackbar.make(
                binding.root,
                getString(R.string.check_your_location),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            val snackbar = Snackbar.make(
                binding.root,
                getString(R.string.access_geo_prohibited),
                Snackbar.LENGTH_SHORT
            )
            snackbar.setAction(getString(R.string.ok)) {
                snackbar.dismiss()
            }.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    when (event) {
                        BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION,
                        BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                            viewModel.currtntLocation.observe(viewLifecycleOwner) {
                                move(it, getString(R.string.go_to_netology))
                            }
                        }

                        else -> {}
                    }
                }
            })
            snackbar.show()
        }
    }

    private fun move(point: Point, message: String = "") {
        if (compareLocations(map.cameraPosition, point)) return

        map.move(
            CameraPosition(point, 17.0f, 150.0f, 30.0f),
            Animation(Animation.Type.SMOOTH, 5F),
            null
        )
        if (message.isNotBlank()) {
            requireContext().showToast(message)
        }
    }

    private fun checkPermission(): Boolean {
        val permissions = android.Manifest.permission.ACCESS_FINE_LOCATION
        return when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permissions
            ) == PackageManager.PERMISSION_GRANTED -> {
                MapKitFactory.getInstance().createLocationManager()
                    .requestSingleUpdate(locationListener)
                true

            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), permissions
            ) -> {
                false
            }

            else -> {
                requestPermissionLauncher.launch(permissions)
                true
            }
        }
    }
}