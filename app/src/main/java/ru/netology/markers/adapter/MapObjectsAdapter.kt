package ru.netology.markers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.markers.databinding.MapObjectCardBinding
import ru.netology.markers.dto.LocalMapObject

class MapObjectsAdapter() : ListAdapter<LocalMapObject, ObjectVieweHolder>(MapObjectDiffCallback) {
    override fun onBindViewHolder(holder: ObjectVieweHolder, position: Int) {
        val localMapObject = getItem(position)
        holder.bind(localMapObject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectVieweHolder {
        val binding =
            MapObjectCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ObjectVieweHolder(binding)
    }

}

class ObjectVieweHolder(private val binding: MapObjectCardBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(localMapObject: LocalMapObject) {
        binding.name.text = localMapObject.name
        binding.descriptin.text = localMapObject.description

    }
}

object MapObjectDiffCallback : DiffUtil.ItemCallback<LocalMapObject>() {
    override fun areContentsTheSame(oldItem: LocalMapObject, newItem: LocalMapObject) =
        oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: LocalMapObject, newItem: LocalMapObject) =
        oldItem == newItem

}