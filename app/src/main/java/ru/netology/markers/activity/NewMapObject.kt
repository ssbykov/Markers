package ru.netology.markers.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.markers.R
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

        with(binding) {
            save.setOnClickListener {
                val name = name.text.toString()
                val descriptin = descriptin.text.toString()
                if (name.isNullOrEmpty()) {
                    nameLayout.error = getString(R.string.required)
                    return@setOnClickListener
                } else nameLayout.error = null

                val mapObject = MapObject(
                    latitude = latitude ?: 0.0,
                    longitude = longitude ?: 0.0,
                    name = name,
                    description = descriptin
                )
                viewModel.save(mapObject)
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewMapObject()
    }
}