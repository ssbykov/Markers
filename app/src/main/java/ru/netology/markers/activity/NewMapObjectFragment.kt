package ru.netology.markers.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.markers.R
import ru.netology.markers.databinding.FragmentNewMapObjectBinding
import ru.netology.markers.viewmodel.MapsVeiwModel

@AndroidEntryPoint
class NewMapObjectFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding) {
            viewModel.edited.observe(viewLifecycleOwner) { edited ->
                name.setText(edited.name)
                descriptin.setText(edited.description)
                save.setOnClickListener {
                    val name = name.text.toString()
                    val descriptin = descriptin.text.toString()
                    if (name.isNullOrEmpty()) {
                        nameLayout.error = getString(R.string.required)
                        return@setOnClickListener
                    } else nameLayout.error = null

                    val mapObject = edited.copy(
                        name = name,
                        description = descriptin
                    )
                    viewModel.save(mapObject)
                    with(mapObject) {
                        viewModel.setCurrtntLocation(latitude, longitude, name)
                    }
                    findNavController().navigateUp()
                }
            }


        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewMapObjectFragment()
    }
}