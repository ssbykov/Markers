package ru.netology.markers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.markers.R
import ru.netology.markers.databinding.MapObjectCardBinding
import ru.netology.markers.dto.LocalMapObject

class MapObjectsAdapter(
    private val setupClickListeners: SetupClickListeners
) : ListAdapter<LocalMapObject, ObjectVieweHolder>(MapObjectDiffCallback) {
    override fun onBindViewHolder(holder: ObjectVieweHolder, position: Int) {
        val localMapObject = getItem(position)
        holder.bind(localMapObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectVieweHolder {
        val binding =
            MapObjectCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ObjectVieweHolder(binding, setupClickListeners)
    }

}

class ObjectVieweHolder(
    private val binding: MapObjectCardBinding,
    private val setupClickListeners: SetupClickListeners,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(localMapObject: LocalMapObject) {
        with(binding) {
            name.text = root.resources.getString(R.string.title_name, localMapObject.name)
            descriptin.text =
                root.resources.getString(
                    R.string.card_description,
                    localMapObject.description
                )
            point.text =
                root.resources.getString(
                    R.string.location,
                    String.format("%.6f", localMapObject.latitude),
                    String.format("%.6f", localMapObject.longitude)
                )
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.object_list_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                setupClickListeners.onRemoveListener(localMapObject)
                                true
                            }

                            R.id.edit -> {
                                setupClickListeners.onEditListener(localMapObject)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            root.setOnClickListener {
                setupClickListeners.onItemListener(localMapObject)
            }
        }
    }
}

object MapObjectDiffCallback : DiffUtil.ItemCallback<LocalMapObject>() {
    override fun areContentsTheSame(oldItem: LocalMapObject, newItem: LocalMapObject) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: LocalMapObject, newItem: LocalMapObject) =
        oldItem == newItem

}