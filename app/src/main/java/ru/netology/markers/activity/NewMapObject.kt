package ru.netology.markers.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.markers.databinding.FragmentNewMapObjectBinding
import ru.netology.markers.dto.MapObject
import ru.netology.markers.viewmodel.MapsVeiwModel


private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"

class NewMapObject : Fragment() {

    private lateinit var binding: FragmentNewMapObjectBinding

    private var latitude: Double? = null
    private var longitude: Double? = null

    private val viewModel: MapsVeiwModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewMapObjectBinding.inflate(inflater)

        arguments?.let {
            latitude = it.getDouble(LATITUDE)
            longitude = it.getDouble(LONGITUDE)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.save.setOnClickListener {
                val mapObject = MapObject(
                    latitude = latitude ?: 0.0,
                    longitude = longitude ?: 0.0,
                    name = binding.name.text.toString(),
                    description = binding.descriptin.text.toString()
                )
            viewModel.save(mapObject)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewMapObject()
    }
}