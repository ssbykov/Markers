package ru.netology.markers.InterfaceImpl

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import ru.netology.markers.R
import ru.netology.markers.activity.MapsFragment
import ru.netology.markers.databinding.MapObjectCardBinding
import ru.netology.markers.dto.LocalMapObject
import ru.netology.markers.utils.DialogManager
import ru.netology.markers.viewmodel.MapsVeiwModel

class MapObjectTapListenerImpl(
    private val viewModel: MapsVeiwModel,
    private val fragment: MapsFragment
) : MapObjectTapListener {
    @SuppressLint("StringFormatMatches")
    override fun onMapObjectTap(mapObject: MapObject, p1: Point): Boolean {
        val localMapObjects = viewModel.data.value
        val selectedMapObject = localMapObjects?.find { it.id == mapObject.userData }
        if (selectedMapObject == null) return false
        val context = fragment.requireContext()
        val card = bind(context, selectedMapObject)
        DialogManager.showDialog(context, card.root)
        return true

    }

    private fun bind(context: Context, localMapObject: LocalMapObject): MapObjectCardBinding {
        return MapObjectCardBinding.inflate(fragment.layoutInflater).apply {
            name.text = context.getString(R.string.title_name, localMapObject.name)
            descriptin.text = context.getString(
                R.string.card_description,
                localMapObject.description
            )
            point.text = context.getString(
                R.string.location,
                localMapObject.latitude.toFloat(),
                localMapObject.longitude.toFloat()
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
                                viewModel.removeById(localMapObject.id)
                                DialogManager.dismissDialog()
                                true
                            }

                            R.id.edit -> {
                                viewModel.edit(localMapObject)
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
    }
}