package ru.netology.markers.InterfaceImpl

import android.annotation.SuppressLint
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import ru.netology.markers.R
import ru.netology.markers.activity.MapsFragment
import ru.netology.markers.databinding.MapObjectCardBinding
import ru.netology.markers.utils.DialogManager
import ru.netology.markers.viewmodel.MapsVeiwModel

class MapObjectTapListenerImpl(
    private val viewModel: MapsVeiwModel,
    private val fragment: MapsFragment
) : MapObjectTapListener {
    @SuppressLint("StringFormatMatches")
    override fun onMapObjectTap(mapObject: MapObject, p1: Point): Boolean {
        viewModel.data.observe(fragment.viewLifecycleOwner) { localMapObjects ->
            val selectedMapObject = localMapObjects.find { it.id == mapObject.userData }
            if (selectedMapObject == null) return@observe
            val context = fragment.requireContext()

            val card = MapObjectCardBinding.inflate(fragment.layoutInflater).apply {
                name.text = context.getString(R.string.title_name, selectedMapObject.name)
                descriptin.text = context.getString(
                    R.string.card_description,
                    selectedMapObject.description
                )
                point.text = context.getString(
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
                                    fragment.findNavController().navigate(
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
            DialogManager.showDialog(context, card.root)
        }
        return true

    }
}