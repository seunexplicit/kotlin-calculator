package com.xplicit.calculator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.xplicit.calculator.data.Operators
import com.xplicit.calculator.databinding.CommonButtonBinding
import com.xplicit.calculator.viewmodel.CalculatorViewModel
import com.xplicit.calculator.viewmodel.CalculatorViewModelFactory

class CommonButton:Fragment() {

    private val sharedViewModel:CalculatorViewModel by activityViewModels {
        CalculatorViewModelFactory(requireContext())
    }

    private lateinit var _binding:CommonButtonBinding
    private lateinit var operators: Operators

    val binding get() = _binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = CommonButtonBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operators = Operators(requireContext())
        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = this@CommonButton.viewLifecycleOwner
            removeButton.setOnLongClickListener {
                sharedViewModel.clearExp()
                true
            }
            divideButton.setOnClickListener {
                val operator  = operators.operatorMap()[divideButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
        }
    }

    companion object{
        const val TAG = "Common Button Fragment"
    }
}