package ru.netology.markers.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
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
import ru.netology.markers.R
import ru.netology.markers.databinding.FragmentMapsBinding
import ru.netology.markers.databinding.MapObjectCardBinding
import ru.netology.markers.model.CurrentLocation
import ru.netology.markers.utils.DialogManager
import ru.netology.markers.utils.compareLocations
import ru.netology.markers.utils.showToast
import ru.netology.markers.viewmodel.MapsVeiwModel
import ru.netology.markers.viewmodel.empty

class MapsFragment : Fragment() {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapView: MapView
    private lateinit var map: Map
    private lateinit var toast: Toast
    private val viewModel: MapsVeiwModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
            .addOnFailureListener { _ ->
                requireContext().showToast(getString(R.string.location_not_determined))
            }
    }

    val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            }
        }


    @SuppressLint("StringFormatMatches")
    private val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        viewModel.data.observe(viewLifecycleOwner) { localMapObjects ->
            val selectedMapObject = localMapObjects.find { it.id == mapObject.userData }
            if (selectedMapObject == null) return@observe
            val card = MapObjectCardBinding.inflate(layoutInflater).apply {
                name.text = requireContext().getString(R.string.title_name, selectedMapObject.name)
                descriptin.text = requireContext().getString(
                    R.string.card_description,
                    selectedMapObject.description
                )
                point.text = requireContext().getString(
                    R.string.location,
                    selectedMapObject.latitude.toFloat(),
                    selectedMapObject.longitude.toFloat()
                )
                root.setOnClickListener {
                    DialogManager.dismissDialog()
                }
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.object_list_menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    viewModel.removeById(selectedMapObject.id)
                                    DialogManager.dismissDialog()
                                    true
                                }

                                R.id.edit -> {
                                    viewModel.edit(selectedMapObject)
                                    findNavController().navigate(
                                        R.id.action_mapsFragment_to_newMapObject
                                    )
                                    dismiss()
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }
            }
            DialogManager.showDialog(requireContext(), card.root)
        }
        true
    }

    private val mapObjectDragListener = object : MapObjectDragListener {
        var point = Point()
        override fun onMapObjectDragStart(mapObject: MapObject) {}

        override fun onMapObjectDrag(mapObject: MapObject, p1: Point) {
            point = p1
        }

        override fun onMapObjectDragEnd(mapObject: MapObject) {
            val selectedMapObject = viewModel.getById(mapObject.userData as Long)
            if (selectedMapObject != null && (point.latitude != 0.0 || point.longitude != 0.0)) {
                val localMapObject = selectedMapObject.copy(
                    id = selectedMapObject.id,
                    latitude = point.latitude,
                    longitude = point.longitude
                )
                viewModel.save(localMapObject)
                point = Point()
            }
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
        MapKitFactory.initialize(requireContext())

        viewModel.data.observe(viewLifecycleOwner) { objects ->
            map.mapObjects.clear()
            objects.forEach {
                val imageProvider =
                    ImageProvider.fromResource(context, R.drawable.ic_dollar_pin)
                val placemarkObject = map.mapObjects.addPlacemark().apply {
                    geometry = Point(it.latitude, it.longitude)
                    setIcon(imageProvider)
                    setTextStyle(TEXT_STYLE)
                    userData = it.id
                    setText(it.name)
                    isDraggable = true
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
        mapkitVersionTextView.text = MapKitFactory.getInstance().version
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
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        DialogManager.dismissDialog()
        map.cameraPosition.target.apply {
            viewModel.setCurrtntLocation(latitude, longitude, getString(R.string.last_location))
        }
        super.onStop()
    }


    companion object {
        private val TEXT_STYLE = TextStyle().apply {
            placement = TextStyle.Placement.RIGHT
        }

        @JvmStatic
        fun newInstance() = MapsFragment()
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