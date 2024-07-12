package ru.netology.markers.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectDragListener
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.markers.InterfaceImpl.MapObjectDragListenerImpl
import ru.netology.markers.InterfaceImpl.MapObjectTapListenerImpl
import ru.netology.markers.R
import ru.netology.markers.databinding.FragmentMapsBinding
import ru.netology.markers.databinding.MapObjectCardBinding
import ru.netology.markers.model.CurrentLocation
import ru.netology.markers.utils.DialogManager
import ru.netology.markers.utils.compareLocations
import ru.netology.markers.utils.showToast
import ru.netology.markers.viewmodel.MapsVeiwModel
import ru.netology.markers.viewmodel.empty
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment() {

    @Inject
    lateinit var mapKitFactory: MapKit
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: MapView
    private lateinit var map: Map

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: MapsVeiwModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private val mapObjectDragListener by lazy { MapObjectDragListenerImpl(viewModel) }
    private val placemarkTapListener by lazy { MapObjectTapListenerImpl(viewModel, this) }

    companion object {
        private val TEXT_STYLE = TextStyle().apply {
            placement = TextStyle.Placement.RIGHT
        }

        @JvmStatic
        fun newInstance() = MapsFragment()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.setCurrtntLocation(
                        location.latitude,
                        location.longitude,
                        getString(R.string.current_location)
                    )
                }
            }
    }

    val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            }
        }


    val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {}

        override fun onMapLongTap(map: Map, point: Point) {
            val mapObject = empty.copy(latitude = point.latitude, longitude = point.longitude)
            viewModel.edit(mapObject)
            findNavController().navigate(R.id.action_mapsFragment_to_newMapObject)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater)

        viewModel.data.observe(viewLifecycleOwner) { objects ->
            map.mapObjects.clear()
            objects.forEach {
                val imageProvider =
                    ImageProvider.fromResource(context, R.drawable.ic_dollar_pin)
                val placemarkObject = map.mapObjects.addPlacemark()
                    .apply {
                        geometry = Point(it.latitude, it.longitude)
                        userData = it.id
                        isDraggable = true
                        setIcon(imageProvider)
                        setTextStyle(TEXT_STYLE)
                        setText(it.name)
                        setDragListener(mapObjectDragListener)
                    }

                placemarkObject.addTapListener(placemarkTapListener)
            }
        }
        viewModel.currtntLocation.observe(viewLifecycleOwner) { location ->
            move(location)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapview
        val mapkitVersionTextView = binding.mapkitVersion.mapkitVersionValue
        mapkitVersionTextView.text = mapKitFactory.version
        map = mapView.mapWindow.map
        map.addInputListener(inputListener)
        binding.location.setOnClickListener {
            if (!setLocation()) {
                requireContext().showToast(getString(R.string.access_geo_prohibited))
            }
        }

        binding.listObjects.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_feedObjects)
        }

    }

    override fun onStart() {
        super.onStart()
        mapKitFactory.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        mapKitFactory.onStop()
        DialogManager.dismissDialog()
        map.cameraPosition.target.apply {
            viewModel.setCurrtntLocation(latitude, longitude, getString(R.string.last_location))
        }
        super.onStop()
    }

    private fun move(currentLocation: CurrentLocation) {
        if (compareLocations(map.cameraPosition, currentLocation.point)) return

        map.move(
            CameraPosition(currentLocation.point, 17.0f, 150.0f, 30.0f),
            Animation(Animation.Type.SMOOTH, 5F),
            null
        )
        if (!currentLocation.name.isNullOrBlank()) {
            requireContext().showToast(
                requireContext().getString(
                    R.string.move_to_location,
                    currentLocation.name
                )
            )
        }
    }

    private fun setLocation(): Boolean {
        val permissions = android.Manifest.permission.ACCESS_FINE_LOCATION
        return when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permissions
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
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