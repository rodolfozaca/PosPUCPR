package com.rodolfoz.textaiapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rodolfoz.textaiapp.R
import com.rodolfoz.textaiapp.databinding.FragmentPersonalDataBinding

class PersonalDataFragment : Fragment() {

    private lateinit var binding: FragmentPersonalDataBinding

    private val viewModel: PersonalDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonalDataBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setListeners()

        return binding.root
    }

    private fun setListeners() {

        binding.apply {

            btnConfirm.setOnClickListener {
                findNavController().navigate(R.id.action_personalDataFragment_to_promptAndResponseFragment)
            }
        }
    }
}
