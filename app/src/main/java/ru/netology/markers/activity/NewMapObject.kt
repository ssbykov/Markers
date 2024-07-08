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


class NewMapObject : Fragment() {

    private lateinit var binding: FragmentNewMapObjectBinding
    private val viewModel: MapsVeiwModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewMapObjectBinding.inflate(inflater)

        return binding.root
    }



    companion object {
        @JvmStatic
        fun newInstance() = NewMapObject()
    }
}