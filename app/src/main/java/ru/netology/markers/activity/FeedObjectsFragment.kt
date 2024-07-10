package ru.netology.markers.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.geometry.Point
import ru.netology.markers.adapter.MapObjectsAdapter
import ru.netology.markers.adapter.SetupClickListeners
import ru.netology.markers.databinding.FragmentFeedObjectsBinding
import ru.netology.markers.dto.LocalMapObject
import ru.netology.markers.viewmodel.MapsVeiwModel


class FeedObjectsFragment : Fragment() {
    private lateinit var binding: FragmentFeedObjectsBinding
    private val viewModel: MapsVeiwModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedObjectsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MapObjectsAdapter(object : SetupClickListeners {
            override fun onItemListener(localMapObject: LocalMapObject) {
                val point = Point(localMapObject.latitude, localMapObject.longitude)
                viewModel.setCurrtntLocation(point)
                findNavController().navigateUp()
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { objects ->
            adapter.submitList(objects)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = FeedObjectsFragment()
    }

}