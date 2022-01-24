package com.example.calculator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.calculator.databinding.CommonButtonBinding
import com.example.calculator.viewmodel.CalculatorViewModel

class CommonButton:Fragment() {

    private val viewModel:CalculatorViewModel by activityViewModels()
    private lateinit var _binding:CommonButtonBinding
    val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CommonButtonBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = this@CommonButton.viewModel
        }
    }

    companion object{
        val TAG = "Common Scope"
    }
}